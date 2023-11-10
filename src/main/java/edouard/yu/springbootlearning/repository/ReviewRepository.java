package edouard.yu.springbootlearning.repository;

import edouard.yu.springbootlearning.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByRating(int rating);
}
