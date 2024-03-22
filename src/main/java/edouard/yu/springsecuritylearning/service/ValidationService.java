package edouard.yu.springsecuritylearning.service;

import edouard.yu.springsecuritylearning.entity.User;
import edouard.yu.springsecuritylearning.entity.Validation;
import edouard.yu.springsecuritylearning.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;

    public void register(User user) {
        Validation validation = new Validation();
        validation.setUser(user);

        Instant createdAt = Instant.now();
        validation.setCreatedAt(createdAt);

        Instant expiredAt = createdAt.plus(10, ChronoUnit.MINUTES);
        validation.setExpiredAt(expiredAt);

        Random random = new Random();
        int randomInteger = random.nextInt(1_000_000);
        String activationCode = String.format("%06d", randomInteger);
        validation.setActivationCode(activationCode);

        this.validationRepository.save(validation);
        this.notificationService.sendEmail(validation);
    }

    public Validation findByActivationCode(String activationCode) {
        return this.validationRepository.findByActivationCode(activationCode).orElseThrow(() -> new RuntimeException("Invalid activation code"));
    }

    @Scheduled(cron = "@hourly")
    public void removeExpiredActivationCode() {
        log.info("Deletion of expired activation codes at: {}", Instant.now());
        this.validationRepository.deleteAllByExpiredAtBefore(Instant.now());
    }
}
