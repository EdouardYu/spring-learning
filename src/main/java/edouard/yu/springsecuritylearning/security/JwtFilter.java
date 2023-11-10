package edouard.yu.springsecuritylearning.security;

import edouard.yu.springsecuritylearning.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Service
public class JwtFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain) throws ServletException, IOException {
        String token;
        String email = null;
        boolean isTokenExpired = true;

        // On récupère le token JWT et les informations qu'il contient dans le champ "Authorization" du header de la requête
        final String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            isTokenExpired = this.jwtService.isTokenExpired(token);
            email = this.jwtService.extractEmail(token);
        }

        // Si le token JWT n'a pas expiré, qu'on trouve bien un email dans les claims du token et qu'il n'y a pas encore de contexte de sécurité,
        // c'est-à-dire qu'il n'y a encore personne d'authentifié pour le moment
        // On va pouvoir dire à spring security qu'on va utiliser les informations du token pour authentifier l'utilisateur (en occurrence l'email ici)
        if(!isTokenExpired && email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // on passe le token d'authentification à spring security (dans le contexte de sécurité)
        }

        assert filterChain != null;
        filterChain.doFilter(request, response); // permet à spring security de continuer à filtrer en passant dans d'autres filtres
        // marche un peu comme next(req, res) avec Express.js
    }
}
