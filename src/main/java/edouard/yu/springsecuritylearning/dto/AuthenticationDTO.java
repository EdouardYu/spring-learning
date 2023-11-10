package edouard.yu.springsecuritylearning.dto;

public record AuthenticationDTO(
        String email,
        String password
) {
}
