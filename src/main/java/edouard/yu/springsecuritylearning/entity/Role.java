package edouard.yu.springsecuritylearning.entity;

import edouard.yu.springsecuritylearning.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING) // permet de convertir une enumération en string ici
    // Par défaut, c'est EnumType.ORDINAL, Enumerated convertie la enumération en nombre
    private RoleType label = RoleType.USER;
}
