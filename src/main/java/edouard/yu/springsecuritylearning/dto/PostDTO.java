package edouard.yu.springsecuritylearning.dto;

import java.util.Date;

public record PostDTO(
        int id,
        String title,
        String content,
        Date lastUpdate
) {
}
