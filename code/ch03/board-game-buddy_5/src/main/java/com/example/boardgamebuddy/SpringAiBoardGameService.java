package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  private final ChatClient chatClient;
  private final GameRulesService gameRulesService;

  public SpringAiBoardGameService(
      ChatClient.Builder chatClientBuilder,
      GameRulesService gameRulesService) {
    this.chatClient = chatClientBuilder.build();
    this.gameRulesService = gameRulesService;
  }

  @Value("classpath:/promptTemplates/systemPromptTemplate.st")
  Resource promptTemplate;

  // tag::streamingAsk[]
  @Override
  public Flux<String> askQuestion(Question question) {   // <1>
    String gameRules = gameRulesService.getRulesFor(question.gameTitle());

    return chatClient.prompt()
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle())
            .param("rules", gameRules))
        .user(question.question())
        .stream()         // <2>
        .content();
  }
  // end::streamingAsk[]

}
