package edouard.yu.springdatalearning.repository;

import edouard.yu.springdatalearning.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    // Les trois méthodes suivantes sont identiques :
    //Iterable<User> findByNameContaining(String nameSequence);
    @Query("FROM User u WHERE u.name LIKE %?1%") // "u" devient l'alias de l'entité User (ne pas prendre le nom de la table, mais la classe Java)
    // "?1" représente le premier query param de notre endpoint
    Iterable<User> getByNameSequence(String nameSequence);

    //@Query(value = "SELECT * FROM user WHERE name LIKE %?1%", nativeQuery = true) // contrairement à la requête précédente,
    // ici, on fait des requêtes SQL native grâce au paramètre nativeQuery et par conséquent la value du query devient du SQL natif
    //Iterable<User> getByNameSequenceUsingNativeQuery(String nameSequence);
}
