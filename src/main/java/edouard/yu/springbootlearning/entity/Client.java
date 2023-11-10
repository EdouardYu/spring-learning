package edouard.yu.springbootlearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true) // ssi on n'a pas spécifié que la colonne de la table contient des valeurs uniques lors de sa création
    private String email;
    private String phone;
    private Date createdAt;
    private Date lastUpdate;

}
