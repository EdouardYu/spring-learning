package edouard.yu.springbootlearning.mapper;

import edouard.yu.springbootlearning.dto.ClientDTO;
import edouard.yu.springbootlearning.entity.Client;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component // Annotation générique qui est hérité par @Service et toutes les autres annotations qui permettent de créer des beans sur Spring
// Un bean est une méthode qu'on peut instancier
public class ClientDTOMapper implements Function<Client, ClientDTO> {
    @Override
    public ClientDTO apply(Client client) {
        return new ClientDTO(client.getId(), client.getEmail(), client.getPhone());
    }
}
