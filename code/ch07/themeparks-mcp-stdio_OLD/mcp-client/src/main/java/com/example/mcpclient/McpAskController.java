package com.example.mcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.McpToolCallback;
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

// end::useSystemTemplate[]

    /*
    // tag::useSystemTemplate[]

    ...

    // end::useSystemTemplate[]
     */

  private final ChatClient chatClient;

  public McpAskController(ChatClient.Builder chatClientBuilder, McpToolCallback[] toolCallbacks) {
    this.chatClient = chatClientBuilder
        .defaultTools(toolCallbacks)
        .build();
  }

  // tag::useSystemTemplate[]
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

}
// end::useSystemTemplate[]
