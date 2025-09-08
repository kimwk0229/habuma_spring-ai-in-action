// tag::stringTemplate[]
package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
// end::stringTemplate[]
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
// tag::stringTemplate[]
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  private final ChatClient chatClient;

  public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder) { // <1>
    this.chatClient = chatClientBuilder.build(); // <2>
  }

  // end::stringTemplate[]

  /*
  // tag::stringTemplate[]
  private static final String questionPromptTemplate = """ // <1>
      Answer this question about {game}: {question}
      """;
  // end::stringTemplate[]
  */

  // tag::resourceTemplate[]
  @Value("classpath:/promptTemplates/questionPromptTemplate.st")
  Resource questionPromptTemplate;
  // end::resourceTemplate[]
  // tag::stringTemplate[]

  @Override
  public Answer askQuestion(Question question) {
    String answerText = chatClient.prompt()
        .user(userSpec -> userSpec
            .text(questionPromptTemplate)      // <2>
            .param("gameTitle", question.gameTitle())
            .param("question", question.question()))   // <3>
        .call()
        .content();     // <4>

    return new Answer(question.gameTitle(), answerText);
  }

}
// end::stringTemplate[]
