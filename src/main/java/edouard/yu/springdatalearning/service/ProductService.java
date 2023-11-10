package edouard.yu.springdatalearning.service;

import com.github.javafaker.Faker;
import edouard.yu.springdatalearning.entity.Opinion;
import edouard.yu.springdatalearning.entity.Product;
import edouard.yu.springdatalearning.entity.User;
import edouard.yu.springdatalearning.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@AllArgsConstructor
@Service
public class ProductService {
    private ProductRepository productRepository;

    public Iterable<Product> search(String name, String charSequence, String minPrice, String commentSequence) {
        if (Strings.isNotEmpty(name)) {
            return productRepository.findByName(name);
        } else if(Strings.isNotEmpty(charSequence)) {
            return productRepository.findByNameContainingOrderByPriceDesc(charSequence);
        } else if (Strings.isNotEmpty(minPrice)) {
            return productRepository.findByPriceAfter(Integer.parseInt(minPrice));
        } else if (Strings.isNotEmpty(commentSequence)) {
            return productRepository.findByOpinionsCommentContaining(commentSequence);
        }

        return this.productRepository.findAll();
    }

    //fonction permettant d'initialiser 70 produits dans la table de la bdd
    public void initializeProducts(){
        Faker faker = new Faker();
        final List<Product> products = IntStream.range(30, 100).mapToObj(index -> {
            User user = User.builder().name(faker.name().fullName()).build();

            List<Opinion> opinions = IntStream.range(2, 5).mapToObj(i ->
                    Opinion.builder()
                            .user(user).rating(4)
                            .comment(faker.lorem().sentence())
                            .createAt(Instant.now())
                            .build()
            ).toList();

            return Product.builder()
                    .name("Product" + index)
                    .price(index * 10)
                    .opinions(opinions)
                    .build();
        }).toList();

        this.productRepository.saveAll(products);
    }
}
