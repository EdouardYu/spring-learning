package edouard.yu.springbootlearning.service;

import edouard.yu.springbootlearning.dto.ClientDTO;
import edouard.yu.springbootlearning.entity.Client;
import edouard.yu.springbootlearning.mapper.ClientDTOMapper;
import edouard.yu.springbootlearning.repository.ClientRepository;
//import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class ClientService {
    //@Autowired // Permet d'injecter le clientRepository directement,
    // mais déconseillé, car même si on n'injecte pas d'élément, en faisant les tests unitaires,
    // les tests continueront à passer. Il faut passer par le constructeur pour que les tests suivent les évolutions.
    //ClientRepository clientRepository;

    // On pourra également utiliser @AllArgsConstructor de lombok pour son constructeur :
    private final ClientDTOMapper clientDTOMapper;
    private final ClientRepository clientRepository;

    public void create(Client client) {
        // ssi on n'a pas spécifié que la colonne de la table contient des valeurs uniques
        // lors de sa création et dans entity Client
        // Cependant cette méthode permet de gérer plus facilement les exceptions au lieu d'avoir une erreur 500 par défaut
        Client bddClient = this.clientRepository.findByEmail(client.getEmail());
        if (bddClient == null)
            this.clientRepository.save(client);
    }

    public Stream<ClientDTO> searchAll() {
        return this.clientRepository.findAll()
                .stream().map(clientDTOMapper);
    }

    public Client search(int id) {
        Optional<Client> optionalClient = this.clientRepository.findById(id);
        return optionalClient.orElseThrow(
                () -> new EntityNotFoundException("No customer exists with this ID")
                // Bien sûr, il ne faut pas mettre le message directement ici comme quoi le client avec cette ID n'existe pas, cela provoquerait une faille de sécurité.
                // Il faut faire de la gestion des messages d'erreur et mettre au pire des cas un message plus générique et moins explicite ici.
        );
    }

    public Client searchOrCreate(Client client) {
        Client bddClient = this.clientRepository.findByEmail(client.getEmail());
        if (bddClient == null)
            bddClient = this.clientRepository.save(client);
        return bddClient;
    }

    public void update(int id, Client client) {
        Client bddClient = this.search(id);
        if (bddClient.getId() == client.getId()) {
            bddClient.setEmail(client.getEmail());
            bddClient.setPhone(client.getPhone());
            this.clientRepository.save(bddClient);
        }
    }
}
