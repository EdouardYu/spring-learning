package edouard.yu.springdatalearning.service;

import edouard.yu.springdatalearning.entity.User;
import edouard.yu.springdatalearning.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;

    public Iterable<User> search(String nameSequence) {
        if (Strings.isNotEmpty(nameSequence)) {
            return userRepository.getByNameSequence(nameSequence);
        }

        return this.userRepository.findAll();
    }
}
