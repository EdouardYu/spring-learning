package edouard.yu.springsecuritylearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "activated_at")
    private Instant activatedAt;
    @Column(name = "activation_code")
    private String activationCode;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
