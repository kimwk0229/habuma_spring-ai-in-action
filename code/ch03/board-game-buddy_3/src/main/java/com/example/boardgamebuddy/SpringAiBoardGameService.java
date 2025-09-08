package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

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

  // tag::systemPromptTemplate[]
  @Value("classpath:/promptTemplates/systemPromptTemplate.st")
  Resource promptTemplate;
  // end::systemPromptTemplate[]

  // tag::askMethod[]
  @Override
  public Answer askQuestion(Question question) {
    String gameRules = gameRulesService.getRulesFor(question.gameTitle());

    String answerText = chatClient.prompt()
        .system(systemSpec -> systemSpec       // <1>
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle())
            .param("rules", gameRules))
        .user(question.question())             // <2>
        .call()
        .content();

    return new Answer(question.gameTitle(), answerText);
  }
  // end::askMethod[]

}
