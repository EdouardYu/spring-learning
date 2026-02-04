package edouard.yu.springgptlearning.dto;

import java.util.List;

public record RoadmapStep(Integer step, String label, String description, List<String> skills) {
}
