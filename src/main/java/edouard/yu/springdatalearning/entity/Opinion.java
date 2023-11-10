package edouard.yu.springdatalearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "opinion")
public class Opinion {
    @Id
    @GeneratedValue(strategy = IDENTITY) // c'est la bdd qui se charge de comment générer la clé primaire
    private int id;
    private int rating;
    private String comment;
    private Instant createAt;
    @ManyToOne(cascade = CascadeType.ALL) // Possibilité plusieurs lignes de la table opinion peuvent avoir le même utilisateur
    // On peut mettre la cascade en ALL pour avoir tous les rôles et ainsi, il peut faire le PERSIST, le MERGE le DETACH, le REFRESH et le REMOVE.
    // Sinon Spring lève une exception "save the transient instance before flushing",
    // Comme on n'a pas renseigné de clé primaire, transient signifie que Spring ne peut pas ajouter un utilisateur dans la colonne de la table parce qu'il ne le connait pas.
    // Il faut alors créer l'utilisateur s'il n'existe pas et puis l'ajouter dans la colonne de la table produit, c'est ce que fait le PERSIST
    // Si l'utilisateur existe déjà dans la bdd, on ne le recrée pas, mais on va juste récupérer les informations manquantes comme la clé primaire, fusionner les informations
    // et l'ajouter dans la colonne de la table produit, c'est ce que fait le MERGE
    @JoinColumn(name = "user_id") // par défaut, la relation se fait automatiquement avec la clé primaire de cette table donc pas obligatoire juste pour rendre explicite
    private User user;
}

