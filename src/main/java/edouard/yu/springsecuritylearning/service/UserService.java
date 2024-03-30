package edouard.yu.springsecuritylearning.service;

import edouard.yu.springsecuritylearning.dto.UserDTO;
import edouard.yu.springsecuritylearning.entity.Role;
import edouard.yu.springsecuritylearning.entity.User;
import edouard.yu.springsecuritylearning.entity.Validation;
import edouard.yu.springsecuritylearning.exception.AlreadyProcessedException;
import edouard.yu.springsecuritylearning.mapper.UserDTOMapper;
import edouard.yu.springsecuritylearning.repository.UserRepository;
import edouard.yu.springsecuritylearning.repository.ValidationRepository;
import edouard.yu.springsecuritylearning.validator.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository;

    public void signUp(UserDTO userDTO) {
        if(!EmailValidator.validEmail(userDTO.email())) {
            throw new RuntimeException("Invalid email");
        }

        Optional<User> dbUser = this.userRepository.findByEmail(userDTO.email());
        if(dbUser.isPresent()) {
            throw new RuntimeException("Email already used");
        }

        dbUser = this.userRepository.findByUsername(userDTO.username());
        if(dbUser.isPresent()) {
            throw new RuntimeException("Username already used");
        }

        String encryptedPassword = this.passwordEncoder.encode(userDTO.password());

        User user = this.userRepository.save(new User(
                userDTO.id(),
                encryptedPassword,
                userDTO.username(),
                userDTO.email(),
                false,
                new Role()
        ));

        this.validationService.register(user);
    }

    public void activate(Map<String, String> activation) {
        Validation validation = this.validationService.findByActivationCode(activation.get("activationCode"));
        if(Instant.now().isAfter(validation.getExpiredAt())) {
            throw new RuntimeException("Expired activation code");
        }

        User user = this.userRepository.findById(validation.getUser().getId()).orElseThrow(() -> new RuntimeException("Unknown user"));

        if(user.isEnabled()) {
            throw new AlreadyProcessedException("User already enabled");
        }

        user.setEnabled(true);
        this.userRepository.save(user);
        validation.setActivatedAt(Instant.now());
        this.validationRepository.save(validation);
    }

    //méthode de l'interface UserDetailsService permettant de chercher un utilisateur dans la base de données en fonction du login et du mot de passe qu'on aura donnés
    // et les comparer au login et mot de passe crypté de la base de données (il fait donc également le décryptage)
    @Override
    public User loadUserByUsername(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Comme la méthode pour envoyer un code d'activation
    public void resetPassword(Map<String, String> parameters) {
        User user = this.loadUserByUsername(parameters.get("email"));
        this.validationService.register(user);
    }

    public void newPassword(Map<String, String> parameters) {
        User user = this.loadUserByUsername(parameters.get("email"));
        Validation validation = validationService.findByActivationCode(parameters.get("activationCode"));

        // Si l'utilisateur qui réinitialise le mot de passe est le même que celui de l'email,
        // on modifie le mot de passe encrypté de la bdd
        if(validation.getUser().getEmail().equals(user.getEmail())) {
            String encryptedPassword = this.passwordEncoder.encode(parameters.get("password"));
            user.setPassword(encryptedPassword);
            this.userRepository.save(user);
        }
    }

    public Stream<UserDTO> searchAll() {
        return StreamSupport.stream(this.userRepository.findAll().spliterator(), false)
                .map(this.userDTOMapper);
    }
}
