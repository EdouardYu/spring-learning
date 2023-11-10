package edouard.yu.springdatalearning.controller;

import edouard.yu.springdatalearning.entity.Product;
import edouard.yu.springdatalearning.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping(path = "product", produces = APPLICATION_JSON_VALUE) // l'endpoint de la requête http sera /product et ce endpoint retourne les valeurs sous forme de json
public class ProductController {
    private ProductService productService;
    @GetMapping
    public Iterable<Product> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String charSequence,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String commentSequence
            ) {
        return this.productService.search(name, charSequence, minPrice, commentSequence);
    }
}
