package edouard.yu.springactuatorlearning.kpi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCount(
    @JsonProperty("client_count")
    Integer clientCount
) {
}
