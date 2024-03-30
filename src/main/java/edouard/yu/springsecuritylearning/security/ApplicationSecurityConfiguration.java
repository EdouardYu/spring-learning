package edouard.yu.springsecuritylearning.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@EnableMethodSecurity // pour ajouter des méthodes avec des permissions dans le controller directement
// Fonctionne avec @PreAuthorize dans les controllers
@AllArgsConstructor
@Configuration // signifie que cette classe est destinée à la configuration de l'application
// Rem : la @Configuration est instancié au démarrage de l'application
@EnableWebSecurity // avec @Configuration, signifie que cette classe est destinée à la configuration de sécurité
public class ApplicationSecurityConfiguration /* extends WebSecurityConfiguration : obligatoire pour spring version 2, plus besoin pour la version 3 */ {
    private final JwtFilter jwtFilter;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // À la place de l'extension de WebSecurityConfiguration, on peut, à partir de spring 3, déclarer des beans pour les configurations de sécurité
    // Un bean est une méthode qu'on peut instancier et par exemple, on pourra accéder à l'instance de la classe avec this.securityFilterChain()
    @Bean
    // on retourne une chaine de sécurité SecurityFilterChain
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // On désactive la sécurité CSRF (Cross-Site Request Forgery : falsification de requête inter-site).
        // Une vulnérabilité CSRF est une faille qui permet à un attaquant d'abuser à la fois d'un utilisateur, d'un navigateur web et d'un serveur.
        // Ainsi, en désactivant la partie CSRF, on accepte toutes les requêtes qui ne sont pas du nom de domaine et de même port que celui de l'application
        // On autorise ensuite, uniquement les requêtes POST qui ont un endpoint /signup, /activate, /signin, /token/refresh, /password/reset ou /password/new
        // searchAll n'est autorisé qu'aux administrateurs et managers.
        // Sinon pour toutes autres requêtes, il faut être authentifié
        // Pour s'authentifier, on utilise le token JWT généré grâce à la requête /signin et on va la filtrer grâce à un ensemble de filtres
        // afin de permettre l'utilisation de ce JWT pour authentifier l'utilisateur
        // Pour finir, on build la configuration
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.POST, "/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/activate").permitAll()
                                .requestMatchers(HttpMethod.POST, "/signin").permitAll()
                                .requestMatchers(HttpMethod.POST, "/token/refresh").permitAll()
                                .requestMatchers(HttpMethod.POST, "/password/reset").permitAll()
                                .requestMatchers(HttpMethod.POST, "/password/new").permitAll()
                                //.requestMatchers(HttpMethod.GET, "/post").hasRole("ADMINISTRATOR") // on ne peut renseigner qu'un rôle
                                //.requestMatchers(HttpMethod.GET, "/post").hasAnyRole(RoleType.ADMINISTRATOR.toString(), RoleType.MANAGER.toString()) // on peut renseigner plusieurs rôles
                                // Les permissions font la même chose que les rôles sauf qu'elles sont un peu plus fines,
                                // ex : certains admins et managers ont plus de permissions que les autres administrateurs et managers.
                                // Il faut regarder le champ authority de UserDetails, nous l'avons préfixé avec ROLE_ dans notre projet
                                // On peut aussi faire la même chose avec les annotations directement avec @EnableMethodSecurity et @PreAuthorize dans le controller
                                .requestMatchers(HttpMethod.GET, "/post").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_MANAGER")
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            Map<String, Object> data = new HashMap<>();
                            // Vérifiez si la requête contient un en-tête d'autorisation
                            String authorization = request.getHeader("Authorization");

                            if (authorization != null && authorization.startsWith("Bearer ")) {
                                // Si un en-tête d'autorisation est présent, cela signifie que l'utilisateur a tenté de s'authentifier, mais a échoué, renvoyez donc un 403
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                data.put("error", "Forbidden: Access is denied");
                            } else {
                                // Si aucun en-tête d'autorisation n'est présent, cela signifie que l'accès nécessite une authentification, renvoyez donc un 401
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                data.put("error", "Unauthorized: Authentication is required");
                            }

                            // Écrire les données dans le corps de la réponse
                            response.getWriter().write(this.objectMapper.writeValueAsString(data));
                        })
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> // Comme spring security fonctionne par session,
                         // on ajoute une session qu'on va configurer afin de pouvoir s'en servir pour authentifier, à notre manière, l'utilisateur afin qu'il puisse accéder aux autres endpoints
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // STATELESS permet à spring security, d'une requête à une autre, de ne pas garder les informations des anciennes requêtes permettant de savoir si l'utilisateur s'est déjà authentifié une fois
                        // À chaque requête, on vérifie de nouveau le token JWT
                )
                .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class) // pour chaque requête, avant de l'exécuter, on la passe dans notre filtre
                // le filtre va déterminer si, on ne s'est pas déjà authentifié en amont l'utilisateur
                .build();
    }

    @Bean
    // bean permettant de gérer l'authentification des utilisateurs de l'application, savoir qui est authentifié, qui ne l'est pas
    // On retourne l'AuthenticationManager de la configuration par défaut de spring security
    // D'ailleurs si la méthode n'arrive pas à récupérer l'AuthenticationManager, spring security va la générer
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    //  bean permettant authenticationManager de se connecter à la basse de données
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(); // dao = database access object
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        // userDetailsService est une instance, qui dépend de la méthode loadUserByUsername de UserService,
        // permettant de retourner, s'ils existent, les informations de l'utilisateur qui tente de se connecter
        // et ainsi, permet à la méthode authenticationManager de savoir où chercher les utilisateurs
        // quand un utilisateur tente de se connecter, où authenticationManager va chercher les informations de l'utilisateur
        // pour savoir si le login et le mot de passe correspond à l'utilisateur qu'il est censé être
        daoAuthenticationProvider.setPasswordEncoder(this.bCryptPasswordEncoder); // permet au provider de savoir comment décoder le mot de passe

        return daoAuthenticationProvider;
    }
}
