package edouard.yu.springsecuritylearning;

import edouard.yu.springsecuritylearning.entity.Role;
import edouard.yu.springsecuritylearning.entity.User;
import edouard.yu.springsecuritylearning.enumeration.RoleType;
import edouard.yu.springsecuritylearning.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor // pour initialiser admin et manager
// @EnableScheduling permet de programmer des tâches à un moment de la journée grâce à l'annotation @Scheduled
@EnableScheduling
@SpringBootApplication

// CommandLineRunner permet d'exécuter du code au démarrage du projet
public class SpringSecurityLearningApplication implements CommandLineRunner {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityLearningApplication.class, args);
    }

    // On initialise l'admin et le manager de l'application au démarrage du projet s'ils n'existent pas
    // Autre façon et la bonne façon, c'est de créer directement avec le script sql durant la création des tables
    @Override
    public void run(String... args) {
        if(this.userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(this.passwordEncoder.encode("admin"))
                    .email("yuedouard@outlook.fr")
                    .enabled(true)
                    .role(Role.builder()
                            .label(RoleType.ADMINISTRATOR)
                            .build()
                    )
                    .build();

            this.userRepository.save(admin);
        }

        if(this.userRepository.findByUsername("manager").isEmpty()) {
            User manager = User.builder()
                    .username("manager")
                    .password(this.passwordEncoder.encode("manager"))
                    .email("edyu60656@eleve.isep.fr")
                    .enabled(true)
                    .role(Role.builder()
                            .label(RoleType.MANAGER)
                            .build()
                    )
                    .build();

            this.userRepository.save(manager);
        }
    }
}
