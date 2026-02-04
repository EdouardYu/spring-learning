package edouard.yu.springgptlearning.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class OpenAiConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @Bean
    Resource defineSystemPrompt() {
        return new ClassPathResource("prompt/define-system.st");
    }

    @Bean
    Resource defineQueryPrompt() {
        return new ClassPathResource("prompt/define-query.st");
    }

    @Bean
    Resource roadmapSystemPrompt() {
        return new ClassPathResource("prompt/roadmap-system.st");
    }

    @Bean
    Resource roadmapQueryPrompt() {
        return new ClassPathResource("prompt/roadmap-query.st");
    }
}