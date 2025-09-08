package com.example.mcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// tag::useSystemTemplate[]
@RestController
public class McpAskController {

    @Value("classpath:/prompts/systemPrompt.st")  // <1>
    private Resource systemPromptTemplate;

    private final ChatClient chatClient;

    public McpAskController(ChatClient.Builder chatClientBuilder,
                            ToolCallbackProvider tools) {
        this.chatClient = chatClientBuilder
                .defaultTools(tools)
                .build();
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        Instant now = Instant.now();                 // <2>
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("MM-dd-yyyy")
                .withZone(ZoneId.systemDefault());
        String formattedNow = formatter.format(now);

        return chatClient.prompt()
            .system(systemSpec ->  // <3>
                systemSpec
                    .text(systemPromptTemplate)
                    .param("todaysDate", formattedNow))
            .user(question.question())
            .call()
            .entity(Answer.class);
    }

    // end::useSystemTemplate[]
    public record Question(String question) { }

    public record Answer(String answer) { }
    // tag::useSystemTemplate[]
}
// end::useSystemTemplate[]
