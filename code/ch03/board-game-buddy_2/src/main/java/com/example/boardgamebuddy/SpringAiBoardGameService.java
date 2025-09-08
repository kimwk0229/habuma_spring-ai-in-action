package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

// tag::injectGameRulesService[]
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

  // ...
// end::injectGameRulesService[]


  @Value("classpath:/promptTemplates/questionPromptTemplate.st")
  Resource questionPromptTemplate;

  // tag::stuffThePrompt[]
  @Override
  public Answer askQuestion(Question question) {
    String gameRules = gameRulesService.getRulesFor(question.gameTitle());

    String answerText = chatClient.prompt()
        .user(userSpec -> userSpec
            .text(questionPromptTemplate)
            .param("gameTitle", question.gameTitle())
            .param("question", question.question())
            .param("rules", gameRules))
        .call()
        .content();

    return new Answer(question.gameTitle(), answerText);
  }
  // end::stuffThePrompt[]

  // tag::injectGameRulesService[]
}
// end::injectGameRulesService[]
