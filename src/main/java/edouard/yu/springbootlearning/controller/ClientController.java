package edouard.yu.springbootlearning.controller;

//import edouard.yu.springbootlearning.dto.ErrorEntity;
import edouard.yu.springbootlearning.dto.ClientDTO;
import edouard.yu.springbootlearning.entity.Client;
import edouard.yu.springbootlearning.service.ClientService;
//import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@AllArgsConstructor
@RestController
@RequestMapping(path = "client")
public class ClientController {
    private ClientService clientService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE) // Permet de signifier que la requête consomme du JSON.
    // On peut également le placer au niveau du @RequestMapping mais si on fait cela, toutes les requêtes vont consommer du JSON.
    // Cela pourrait être problématique si on a une requête sans body comme les GET et DELETE ou qui consomme un autre type de données comme le XML.
    public void create(@RequestBody Client client) {
        this.clientService.create(client);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)// Permet de signifier que la requête produit un JSON.
    public Stream<ClientDTO> searchAll() {
        return this.clientService.searchAll();
    }

    // Première méthode pour gérer les exceptions : try catch (gestion d'erreur pour une méthode)
    /*
    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(@PathVariable int id) {
        try {
            return ResponseEntity.ok(this.clientService.search(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorEntity(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage()
                    ));
        }
    }
    */

    // Troisième méthode pour gérer les exceptions : @ControllerAdvice (gestion d'erreur pour tout l'API : recommandé)
    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)// Permet de signifier que la requête produit un JSON.
    public Client search(@PathVariable int id) {
        return this.clientService.search(id);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable int id, @RequestBody Client client) {
        this.clientService.update(id, client);
    }

    // Deuxième méthode pour gérer les exceptions : @ExceptionHandler (gestion d'erreur pour un controller)
    //@ResponseStatus(HttpStatus.NOT_FOUND)
    //@ExceptionHandler({EntityNotFoundException.class}) // permet de définir quels sont les exceptions qu'on traite avec cette méthode
    // On peut bien sûr traiter plusieurs types d'exception
    //public ErrorEntity handleNotFoundException(EntityNotFoundException e) {
    //    return new ErrorEntity(HttpStatus.NOT_FOUND.value(), e.getMessage());
    //}
}
