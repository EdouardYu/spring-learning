package edouard.yu.springsecuritylearning.controller;

import edouard.yu.springsecuritylearning.dto.AuthenticationDTO;
import edouard.yu.springsecuritylearning.dto.UserDTO;
import edouard.yu.springsecuritylearning.security.JwtService;
import edouard.yu.springsecuritylearning.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Stream;

@Slf4j // annotation lombok permettant d'instancier un logger pour la classe
@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void signUp(@RequestBody UserDTO userDTO) {
        this.userService.signUp(userDTO);
        log.info("Successful registration"); // Il serra affiché dans le terminal, quand on fait appel à la méthode
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "password/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void resetPassword(@RequestBody Map<String, String> parameters) {
        this.userService.resetPassword(parameters);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "password/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void newPassword(@RequestBody Map<String, String> parameters) {
        this.userService.newPassword(parameters);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "activate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activate(@RequestBody Map<String, String> activation) { // On peut récupérer le body avec une Map ou un JsonNode à la place d'un DTO pour aller plus vite, mais c'est déconseillé
        this.userService.activate(activation);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "token/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    // On peut également passer l'information dans le header à la place du body
    // Quand l'utilisateur se connecte, on lui génère un couple qui contient le bearer et son refresh
    // Si le bearer expire, l'utilisateur nous envoie cette requête, et on lui génère un nouveau couple
    // Cela va continuer de tourner jusqu'à la date d'expiration soit dépassé dans la table refresh_token de la bdd
    // Rem : c'est intéressant dans la mesure si on veut maintenir un utilisateur connecté
    // comme pour application mobile ou un jeu,
    // mais pas pour une application bancaire où on veut que l'utilisateur se déconnecte
    public @ResponseBody Map<String, String> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        return this.jwtService.refreshToken(refreshTokenRequest);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> signIn(@RequestBody AuthenticationDTO authenticationDTO) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationDTO.email(),
                authenticationDTO.password()
        ));

        log.info("Successful authentication");
        return this.jwtService.generate(authenticationDTO.email());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "signout")
    public void signOut() {
        this.jwtService.signOut();
    }

    // Permission pour administrateur et manager seulement grâce au champ authority de UserDetails
    // Fonctionne avec @EnableMethodSecurity dans ApplicationSecurityController
    // Rem : il est plus judicieux de créer deux controllers, un AuthenticationController et un UserController
    // qui contient les endpoints qui commencent avec /user pour séparer de l'authentification
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR_READ', 'MANAGER_READ')")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Stream<UserDTO> searchAll() {
        return this.userService.searchAll();
    }
}
