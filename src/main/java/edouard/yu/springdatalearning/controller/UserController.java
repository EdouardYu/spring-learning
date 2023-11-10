package edouard.yu.springdatalearning.controller;

import edouard.yu.springdatalearning.entity.User;
import edouard.yu.springdatalearning.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping(path = "user", produces = APPLICATION_JSON_VALUE)
public class UserController {
    private UserService userService;
    @GetMapping
    public Iterable<User> search(
            @RequestParam(required = false) String nameSequence
    ) {
        return this.userService.search(nameSequence);
    }
}

