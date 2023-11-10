package edouard.yu.springsecuritylearning.service;

import edouard.yu.springsecuritylearning.entity.Validation;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public void sendEmail(Validation validation) {
        SimpleMailMessage message = new SimpleMailMessage(); // instanciation d'un objet mail
        message.setFrom("no-reply@spring-security-learning.com"); // définit qui envoie le mail
        message.setTo(validation.getUser().getEmail()); // définit qui reçoit le mail
        message.setSubject("Spring-Security-Learning activation code"); // définit le sujet du mail
        String text = String.format(
                "Here's the activation code to create your Spring-Security-Learning account:\n%s\nThis code is only valid for 10 minutes",
                validation.getActivationCode()
        );
        message.setText(text); // définit le contenu du mail

        this.javaMailSender.send(message); // on envoie le mail de validation
    }
}
