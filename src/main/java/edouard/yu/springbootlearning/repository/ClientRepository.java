package edouard.yu.springbootlearning.repository;

import edouard.yu.springbootlearning.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

//@Repository // facultatif avec extension d'une interface repository de Spring
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByEmail(String email);
}