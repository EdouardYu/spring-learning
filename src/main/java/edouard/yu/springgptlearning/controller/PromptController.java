package edouard.yu.springgptlearning.controller;

import edouard.yu.springgptlearning.dto.DefinePrompt;
import edouard.yu.springgptlearning.dto.RoadmapStep;
import edouard.yu.springgptlearning.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "prompts")
@RequiredArgsConstructor
public class PromptController {
    private final PromptService promptService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "define", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String define(@RequestBody DefinePrompt definePrompt) {
        return this.promptService.define(definePrompt);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "roadmap", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoadmapStep> roadmap(@RequestBody DefinePrompt definePrompt) {
        return this.promptService.roadmap(definePrompt);
    }
}
