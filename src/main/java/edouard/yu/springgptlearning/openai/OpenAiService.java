package edouard.yu.springgptlearning.openai;

import edouard.yu.springgptlearning.dto.DefinePrompt;
import edouard.yu.springgptlearning.dto.RoadmapStep;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiService {
    private final ChatClient chatClient;
    private final Resource defineSystemPrompt;
    private final Resource defineQueryPrompt;
    private final Resource roadmapSystemPrompt;
    private final Resource roadmapQueryPrompt;

    public String define(DefinePrompt definePrompt) {

        Message systemMessage = this.generateMessage(this.defineSystemPrompt);
        Message userMessage = this.generateMessage(this.defineQueryPrompt, Map.of("query", definePrompt.query()));


        return this.chatClient
            .prompt()
            .system(systemMessage.getText())
            .user(userMessage.getText())
            .call()
            .content();
    }

    public List<RoadmapStep> roadmap(DefinePrompt definePrompt) {
        BeanOutputConverter<List<RoadmapStep>> stepsOutputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {
        });

        String format = stepsOutputConverter.getFormat();
        Message systemMessage = this.generateMessage(this.roadmapSystemPrompt, Map.of("query", definePrompt.query()));
        Message userMessage = this.generateMessage(this.roadmapQueryPrompt, Map.of("format", format));


        return this.chatClient
            .prompt()
            .system(systemMessage.getText())
            .user(userMessage.getText())
            .call()
            .entity(new ParameterizedTypeReference<>() {
            });
    }

    private Message generateMessage(Resource resource) {
        PromptTemplate promptTemplate = PromptTemplate.builder().resource(resource).build();
        return promptTemplate.createMessage();
    }

    private Message generateMessage(Resource resource, Map<String, Object> params) {
        PromptTemplate promptTemplate = PromptTemplate.builder().resource(resource).build();
        return promptTemplate.createMessage(params);
    }
}
