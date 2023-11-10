package edouard.yu.springsecuritylearning.dto;

public record UserDTO(
        int id,
        String password,
        String username,
        String email
) {
}
