package edouard.yu.springsecuritylearning.service;

import edouard.yu.springsecuritylearning.dto.PostDTO;
import edouard.yu.springsecuritylearning.entity.Post;
import edouard.yu.springsecuritylearning.entity.User;
import edouard.yu.springsecuritylearning.mapper.PostDTOMapper;
import edouard.yu.springsecuritylearning.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Service
public class PostService {
    private final PostDTOMapper postDTOMapper;
    private final PostRepository postRepository;

    public void create(PostDTO postDTO) {
        Date createdAt = postDTO.lastUpdate();
        // On récupère l'utilisateur connecté dans le contexte de sécurité de spring security
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        this.postRepository.save(new Post(
                postDTO.id(),
                postDTO.title(),
                postDTO.content(),
                createdAt,
                postDTO.lastUpdate(),
                user
        ));
    }

    public Stream<PostDTO> searchAll() {
        return StreamSupport.stream(this.postRepository.findAll().spliterator(), false)
                .map(this.postDTOMapper);
    }
}
