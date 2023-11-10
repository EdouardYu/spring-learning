package edouard.yu.springbootlearning.dto;

public record ErrorEntity(
        int status,
        String message
) {
}

