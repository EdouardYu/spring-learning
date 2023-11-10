package edouard.yu.springdatalearning;

import edouard.yu.springdatalearning.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class SpringDataLearningApplication implements CommandLineRunner {
    private ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataLearningApplication.class, args);
    }

    @Override
    // c'est une commande qui va s'exécuter au lancement de l'application
    public void run(String... args) {
        this.productService.initializeProducts();
    }
}
