package edouard.yu.springsecuritylearning.service;

import edouard.yu.springsecuritylearning.dto.UserDTO;
import edouard.yu.springsecuritylearning.entity.Role;
import edouard.yu.springsecuritylearning.entity.User;
import edouard.yu.springsecuritylearning.entity.Validation;
import edouard.yu.springsecuritylearning.exception.AlreadyProcessedException;
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

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository;

    public void signup(UserDTO userDTO) {
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
        if(Instant.now().isAfter(validation.getExpiresAt())) {
            throw new RuntimeException("Expired activation code");
        }

        User user = this.userRepository.findById(validation.getUser().getId()).orElseThrow(() -> new RuntimeException("Unknown user"));

        if(user.isEnabled()) {
            throw new AlreadyProcessedException("User already enabled");
        }

        user.setEnable(true);
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
}
