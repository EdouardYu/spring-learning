package edouard.yu.springsecuritylearning.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_token")
// Refresh token :
// Méthode 2 : Nouvelle table
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean expired;
    private String value;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "expired_at")
    private Instant expiredAt;
}
