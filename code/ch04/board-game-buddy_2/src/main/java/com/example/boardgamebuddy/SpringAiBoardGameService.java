package com.example.boardgamebuddy;

  // tag::ask[]
  import static org.springframework.ai.chat.client.advisor
                      .QuestionAnswerAdvisor.FILTER_EXPRESSION;

  // end::ask[]

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  private final ChatClient chatClient;

  public SpringAiBoardGameService(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @Value("classpath:/promptTemplates/systemPromptTemplate.st")
  Resource promptTemplate;

  // tag::ask[]
  @Override
  public Answer askQuestion(Question question) {
    String gameNameMatch = String.format(
            "gameTitle == '%s'",
            normalizeGameTitle(question.gameTitle())); // <1>

    return chatClient.prompt()
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .user(question.question())
        .advisors(advisorSpec ->
            advisorSpec.param(FILTER_EXPRESSION, gameNameMatch)) // <2>
        .call()
        .entity(Answer.class);
  }
  // end::ask[]

  private String normalizeGameTitle(String in) {
    return in.toLowerCase().replace(' ', '_');
  }

}
