package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor.FILTER_EXPRESSION;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  private final ChatClient chatClient;

  public SpringAiBoardGameService(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @Value("classpath:/promptTemplates/systemPromptTemplate.st")
  Resource promptTemplate;

  @Override
  public Answer askQuestion(Question question, String conversationId) {
    String gameNameMatch = String.format(
            "gameTitle == '%s'",
            normalizeGameTitle(question.gameTitle()));

    return chatClient.prompt()
        .user(question.question())
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .advisors(advisorSpec -> advisorSpec
            .param(FILTER_EXPRESSION, gameNameMatch)
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
        .call()
        .entity(Answer.class);
  }

  // tag::askQuestionWithImage[]
  @Override
  public Answer askQuestion(Question question,
                            Resource image,            // <1>
                            String imageContentType,   // <1>
                            String conversationId) {
    String gameNameMatch = String.format(
        "gameTitle == '%s'",
        normalizeGameTitle(question.gameTitle()));

    MimeType mediaType =
        MimeTypeUtils.parseMimeType(imageContentType); // <2>

    return chatClient.prompt()
        .user(userSpec -> userSpec
            .text(question.question())
            .media(mediaType, image)) // <3>
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .advisors(advisorSpec -> advisorSpec
            .param(FILTER_EXPRESSION, gameNameMatch)
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
        .call()
        .entity(Answer.class);
  }
  // end::askQuestionWithImage[]

  private String normalizeGameTitle(String in) {
    return in.toLowerCase().replace(' ', '_');
  }

}
