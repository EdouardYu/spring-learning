package edouard.yu.springbootlearning.service;

import edouard.yu.springbootlearning.entity.Client;
import edouard.yu.springbootlearning.entity.Review;
import edouard.yu.springbootlearning.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ReviewService {
    // Les deux attributs suivants s'appellent des beans (bean = une classe qu'on peut instancier)
    private ClientService clientService;
    private ReviewRepository reviewRepository;

    public void create(Review review) {
        Client client = this.clientService.searchOrCreate(review.getClient());
        review.setClient(client);
        this.reviewRepository.save(review);
    }

    public List<Review> searchAll(Integer rating) {
        if (rating != null)
            return this.reviewRepository.findByRating(rating);
        return this.reviewRepository.findAll();
    }

    public void remove(int id) {
        this.reviewRepository.deleteById(id);
    }
}
