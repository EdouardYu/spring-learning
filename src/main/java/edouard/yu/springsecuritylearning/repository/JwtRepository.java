package edouard.yu.springsecuritylearning.repository;

import edouard.yu.springsecuritylearning.entity.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {
    Optional<Jwt> findByValueAndExpiredAndDeactivated(String value, boolean expired, boolean deactivated);

    @Query("FROM Jwt j WHERE j.expired = :expired AND j.deactivated = :deactivated AND j.user.email = :email")
    Optional<Jwt> findUserValideToken(String email, boolean expired, boolean deactivated);

    @Query("FROM Jwt j WHERE j.user.email = :email")
    Stream<Jwt> findUser(String email);

    @Query("FROM Jwt j WHERE j.refreshToken.value = :value")
    Optional<Jwt> findByRefreshToken(String value);

    void deleteAllByExpiredAndDeactivated(boolean expired, boolean deactivated);
}
