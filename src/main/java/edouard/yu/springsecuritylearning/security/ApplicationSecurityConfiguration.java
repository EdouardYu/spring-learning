package edouard.yu.springsecuritylearning.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration // signifie que cette classe est destinée à la configuration de l'application
// Rem : la @Configuration est instancié au démarrage de l'application
@EnableWebSecurity // avec @Configuration, signifie que cette classe est destinée à la configuration de sécurité
public class ApplicationSecurityConfiguration /* extends WebSecurityConfiguration : obligatoire pour spring version 2, plus besoin pour la version 3 */ {
    private final JwtFilter jwtFilter;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // À la place de l'extension de WebSecurityConfiguration, on peut, à partir de spring 3, déclarer des beans pour les configurations de sécurité
    // Un bean est une méthode qu'on peut instancier et par exemple, on pourra accéder à l'instance de la classe avec this.securityFilterChain()
    @Bean
    // on retourne une chaine de sécurité SecurityFilterChain
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // On désactive la sécurité CSRF (Cross-Site Request Forgery : falsification de requête inter-site).
        // Une vulnérabilité CSRF est une faille qui permet à un attaquant d'abuser à la fois d'un utilisateur, d'un navigateur web et d'un serveur.
        // Ainsi, en désactivant la partie CSRF, on accepte toutes les requêtes qui ne sont pas du nom de domaine et de même port que celui de l'application
        // On autorise ensuite, uniquement les requêtes POST qui ont un endpoint /signup, /activate ou /signin
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
                                .anyRequest().authenticated()
                ).sessionManagement(httpSecuritySessionManagementConfigurer -> // Comme spring security fonctionne par session,
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
