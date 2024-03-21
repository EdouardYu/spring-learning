package edouard.yu.springsecuritylearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jwt")
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String value;
    private boolean deactivated;
    private boolean expired;

    // Refresh token :
    // Méthode 1 : Nouveaux attributs
    //@Column(name = "refresh_token")
    //private String refreshToken;
    //@Column(name = "refresh_token_expiration")
    //private Date refreshTokenExpiration;

    // Méthode 2 : Nouvelle table
    // PERSIST permet de dire de créer automatiquement le refreshToken quand on crée le jwt
    // REMOVE permet de dire de supprimer automatiquement le refreshToken quand on supprime le jwt
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private RefreshToken refreshToken;

    // DETACH permet de dire de ne pas supprimer l'utilisateur quand on supprime le jwt
    // MERGE permet de dire qu'il faut que l'utilisateur existe, avant qu'on puisse utiliser le jwt
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;
}
