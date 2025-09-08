// tag::boardGameServiceConstructor[]
package com.example.boardgamebuddy;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor
                .QuestionAnswerAdvisor.FILTER_EXPRESSION;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  // end::boardGameServiceConstructor[]

  /*
  // tag::boardGameServiceConstructor[]
  // ...
  // end::boardGameServiceConstructor[]
   */

  private final ChatClient chatClient;

  public SpringAiBoardGameService(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @Value("classpath:/promptTemplates/systemPromptTemplate.st")
  Resource promptTemplate;

  // tag::askControllerAsk_conversationId[]
  @Override
  public Answer askQuestion(Question question, String conversationId) {
    String gameNameMatch = String.format(
            "gameTitle == '%s'",
            normalizeGameTitle(question.gameTitle()));

    return chatClient.prompt()
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .user(question.question())
        .advisors(advisorSpec -> advisorSpec
            .param(FILTER_EXPRESSION, gameNameMatch)
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
        .call()
        .entity(Answer.class);
  }
  // end::askControllerAsk_conversationId[]

  private String normalizeGameTitle(String in) {
    return in.toLowerCase().replace(' ', '_');
  }

  /*
  // tag::boardGameServiceConstructor[]
  // ...
  // end::boardGameServiceConstructor[]
   */

  // tag::boardGameServiceConstructor[]
}
// end::boardGameServiceConstructor[]
