package edouard.yu.springsecuritylearning.security;

import edouard.yu.springsecuritylearning.entity.Jwt;
import edouard.yu.springsecuritylearning.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Objects;

@AllArgsConstructor
@Service
public class JwtFilter extends OncePerRequestFilter {
    // Ce resolver va nous permettre de faire passer l'information
    // dans le Dispatcher Servlet (qui va lui distribuer la requête dans le bon controller,
    // dans notre cas, on veut faire passer dans ApplicationControllerAdvice)
    // même si la requête a échoué à passer le filtre.
    // Comment ? Avec un try catch dans doFilterInternal
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain) {
        String token;
        Jwt dbJwt = null;
        String email = null;
        boolean isTokenExpired = true;

        try {
            // On récupère le token JWT et les informations qu'il contient dans le champ "Authorization" du header de la requête
            final String authorization = Objects.requireNonNull(request).getHeader("Authorization");

            if(authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                dbJwt = this.jwtService.findTokenByValue(token);
                isTokenExpired = this.jwtService.isTokenExpired(token);
                email = this.jwtService.extractEmail(token);
            }

            // Si le token JWT n'a pas expiré,
            // qu'on trouve bien un email dans les claims du token et qu'il concorde avec le token dans la bdd
            // et qu'il n'y a pas encore de contexte de sécurité,
            // c'est-à-dire qu'il n'y a encore personne d'authentifié pour le moment
            // On va pouvoir dire à spring security qu'on va utiliser les informations du token pour authentifier l'utilisateur (en occurrence l'email ici)
            if(
                    !isTokenExpired
                            && dbJwt.getUser().getEmail().equals(email)
                            && SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                UserDetails userDetails = this.userService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // on passe le token d'authentification à spring security (dans le contexte de sécurité)
            }

            Objects.requireNonNull(filterChain).doFilter(request, response); // permet à spring security de continuer à filtrer en passant dans d'autres filtres
            // marche un peu comme next(req, res) avec Express.js
        } catch (Exception e) {
            // le handler c'est une méthode qui va gérer l'erreur avant de passer dans advice
            handlerExceptionResolver.resolveException(Objects.requireNonNull(request), Objects.requireNonNull(response), null, e);
        }
    }
}
