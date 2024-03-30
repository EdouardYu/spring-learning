package edouard.yu.springsecuritylearning.mapper;

import edouard.yu.springsecuritylearning.dto.UserDTO;
import edouard.yu.springsecuritylearning.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component // Annotation générique qui est hérité par @Service et toutes les autres annotations qui permettent de créer des beans sur Spring
// Un bean est une méthode qu'on peut instancier
public class UserDTOMapper implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail());
    }
}
