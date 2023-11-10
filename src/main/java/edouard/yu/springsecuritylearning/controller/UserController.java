package edouard.yu.springsecuritylearning.controller;

import edouard.yu.springsecuritylearning.dto.AuthenticationDTO;
import edouard.yu.springsecuritylearning.dto.UserDTO;
import edouard.yu.springsecuritylearning.security.JwtService;
import edouard.yu.springsecuritylearning.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j // annotation lombok permettant d'instancier un logger pour la classe
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "signup")
    public void signup(@RequestBody UserDTO userDTO) {
        this.userService.signup(userDTO);
        log.info("Successful registration"); // Il serra affiché dans le terminal, quand on fait appel à la méthode
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "activate")
    public void activate(@RequestBody Map<String, String> activation) { // On peut récupérer le body avec une Map ou un JsonNode à la place d'un DTO pour aller plus vite, mais c'est déconseillé
        this.userService.activate(activation);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "signin")
    public Map<String, String> signin(@RequestBody AuthenticationDTO authenticationDTO) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationDTO.email(),
                authenticationDTO.password()
        ));

        log.info("Successful authentication");
        return this.jwtService.generate(authenticationDTO.email());
    }
}
