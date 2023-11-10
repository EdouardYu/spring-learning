package edouard.yu.springdatalearning.repository;

import edouard.yu.springdatalearning.entity.Product;
import org.springframework.data.repository.CrudRepository; // permet de faire du CRUD
//import org.springframework.data.repository.PagingAndSortingRepository; // permet de faire un findAll avec un tri et selon certaines conditions qu'on fixe
// il faut choisir l'interface selon ce qu'on veut faire, il y en a encore d'autres

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {
    Iterable<Product> findByName(String name);

    Iterable<Product> findByNameContainingOrderByPriceDesc(String charSequence);

    Iterable<Product> findByPriceAfter(int minPrice); // minPrice exclu

    List<Product> findByOpinionsCommentContaining(String commentSequence); // Cherche dans la table product la colonne "opinions"
    // puis cherche une séquence contenue dans la colonne "comment" de la table opinion grâce à la jointure des deux tables en escalier descendant
}
