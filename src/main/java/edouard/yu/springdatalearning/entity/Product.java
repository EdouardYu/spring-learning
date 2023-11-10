package edouard.yu.springdatalearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter // permet de récupérer les valeurs
@Builder // permet de construire les valeurs
@AllArgsConstructor// code implicite d'un constructeur avec tous les attributs pour leur affecter un argument
@NoArgsConstructor // code implicite d'un constructeur sans paramètre
// généralement, il faut toujours générer un constructeur sans champs pour les entités dans Spring
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = IDENTITY) // c'est la bdd qui se charge de comment générer la clé primaire
    private int id;
    private String name;
    private String description;
    private int price;
    @OneToMany(cascade = CascadeType.ALL) // Possibilité d'affectation de plusieurs opinions à un produit dans la table product sous forme de liste ici
    // On peut mettre la cascade en ALL pour avoir tous les rôles et ainsi, il peut faire le PERSIST, le MERGE le DETACH, le REFRESH et le REMOVE.
    // Sinon Spring lève une exception "save the transient instance before flushing",
    // Comme on n'a pas renseigné de clé primaire, transient signifie que Spring ne peut pas ajouter un utilisateur dans la colonne de la table parce qu'il ne le connait pas.
    // Il faut alors créer l'utilisateur s'il n'existe pas et puis l'ajouter dans la colonne de la table produit, c'est ce que fait le PERSIST
    // Si l'utilisateur existe déjà dans la bdd, on ne le recrée pas, mais on va juste récupérer les informations manquantes comme la clé primaire, fusionner les informations
    // et l'ajouter dans la colonne de la table produit, c'est ce que fait le MERGE
    private List<Opinion> opinions;
}




