package edouard.yu.springgptlearning.service;

import edouard.yu.springgptlearning.dto.DefinePrompt;
import edouard.yu.springgptlearning.dto.RoadmapStep;
import edouard.yu.springgptlearning.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final OpenAiService openAiService;

    public String define(DefinePrompt definePrompt) {
        return this.openAiService.define(definePrompt);
    }

    public List<RoadmapStep> roadmap(DefinePrompt definePrompt) {
        return this.openAiService.roadmap(definePrompt);
    }
}
