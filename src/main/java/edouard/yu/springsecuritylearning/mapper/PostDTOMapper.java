package edouard.yu.springsecuritylearning.mapper;

import edouard.yu.springsecuritylearning.dto.PostDTO;
import edouard.yu.springsecuritylearning.entity.Post;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component // Annotation générique qui est hérité par @Service et toutes les autres annotations qui permettent de créer des beans sur Spring
// Un bean est une méthode qu'on peut instancier
public class PostDTOMapper implements Function<Post, PostDTO> {
    @Override
    public PostDTO apply(Post post) {
        return new PostDTO(post.getId(), post.getTitle(), post.getContent(), post.getLastUpdate());
    }
}