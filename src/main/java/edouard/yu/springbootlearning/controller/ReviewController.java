package edouard.yu.springbootlearning.controller;

import edouard.yu.springbootlearning.entity.Review;
import edouard.yu.springbootlearning.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "review")
public class ReviewController {
    private ReviewService reviewService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody Review review) {
        if (review.getRating() > 0 && review.getRating() <= 5)
            this.reviewService.create(review);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Review> searchAll(@RequestParam(required = false)Integer rating) {
        return this.reviewService.searchAll(rating);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{id}")
    public void remove(@PathVariable int id) {
        this.reviewService.remove(id);
    }
}
