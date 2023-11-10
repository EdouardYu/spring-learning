package edouard.yu.springsecuritylearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails /* un utilisateur qui contient des éléments de configuration mis à notre disposition par spring */ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String password;
    private String username;
    private String email;
    private boolean enable = false;
    @OneToOne(cascade = CascadeType.ALL)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Une méthode qui permet de définir les rôles de l'utilisateur, ex : user client, user administrator, etc.
        // On retourne une liste de rôle de l'utilisateur
        // rem : singletonList est une liste immuable serializable avec seulement et toujours qu'un élément
        // rem : il faut toujours préfixer par le mot ROLE_ dans un SimpleGrantedAuthority pour faire comprendre à spring que c'est un rôle
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.getLabel()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() { // est-ce que le compte a expiré
        return this.enable;
    }

    @Override
    public boolean isAccountNonLocked() { // est-ce que le compte est bloqué
        return this.enable;
    }

    @Override
    public boolean isCredentialsNonExpired() { // est-ce que les informations d'identification ont expiré
        return this.enable;
    }

    @Override
    public boolean isEnabled() { // est-ce que le compte est actif
        return this.enable;
    }
}
