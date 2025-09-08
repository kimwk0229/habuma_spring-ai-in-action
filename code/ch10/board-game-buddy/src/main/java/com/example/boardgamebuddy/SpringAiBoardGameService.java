package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.Collection;

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

  // tag::newAskQuestion[]
  @Override
  public Answer askQuestion(Question question, String conversationId) {
    return chatClient.prompt()
        .user(question.question())
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .advisors(advisorSpec -> advisorSpec
            .param(FILTER_EXPRESSION,
                getDocumentMatchExpression(question.gameTitle()))
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
        .call()
        .entity(Answer.class);
  }
  // end::newAskQuestion[]

  @Override
  public Answer askQuestion(Question question,
                            Resource image,
                            String imageContentType,
                            String conversationId) {
    MimeType mediaType =
        MimeTypeUtils.parseMimeType(imageContentType);

    return chatClient.prompt()
        .user(userSpec -> userSpec
            .text(question.question())
            .media(mediaType, image))
        .system(systemSpec -> systemSpec
            .text(promptTemplate)
            .param("gameTitle", question.gameTitle()))
        .advisors(advisorSpec -> advisorSpec
            .param(FILTER_EXPRESSION,
                getDocumentMatchExpression(question.gameTitle()))
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
        .call()
        .entity(Answer.class);
  }

  private String normalizeGameTitle(String in) {
    return in.toLowerCase().replace(' ', '_');
  }

  // tag::getDocumentMatchExpression[]
  private String getDocumentMatchExpression(String gameTitle) {
    return String.format("gameTitle == '%s' %s",
          normalizeGameTitle(gameTitle),
          getPremiumContentFilterExpression());
  }

  private static String getPremiumContentFilterExpression() {
    Collection<? extends GrantedAuthority> authorities =
        SecurityContextHolder.getContext().getAuthentication().getAuthorities();

    if (!authorities.stream().anyMatch(
        a -> a.getAuthority().equals("ROLE_PREMIUM_USER"))) {
      return "AND documentType != 'PREMIUM'";
    }

    return "";
  }
  // end::getDocumentMatchExpression[]

}
