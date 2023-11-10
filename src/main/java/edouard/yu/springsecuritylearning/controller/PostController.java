package edouard.yu.springsecuritylearning.controller;

import edouard.yu.springsecuritylearning.dto.PostDTO;
import edouard.yu.springsecuritylearning.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController // Controller pour gérer les API Rest
@RequestMapping(path = "post")
public class PostController {
    private final PostService postService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody PostDTO postDTO) {
        this.postService.create(postDTO);
    }
}
