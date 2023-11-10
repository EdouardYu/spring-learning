package edouard.yu.springsecuritylearning.repository;

import edouard.yu.springsecuritylearning.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
}


