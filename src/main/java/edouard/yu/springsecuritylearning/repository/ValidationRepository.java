package edouard.yu.springsecuritylearning.repository;

import edouard.yu.springsecuritylearning.entity.Validation;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

public interface ValidationRepository extends CrudRepository<Validation, Integer> {
    Optional<Validation> findByActivationCode(String activationCode);

    void deleteAllByExpiredAtBefore(Instant instant);
}
