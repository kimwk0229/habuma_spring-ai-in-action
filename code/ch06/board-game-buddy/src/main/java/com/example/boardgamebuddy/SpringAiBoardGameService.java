package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

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

  private String normalizeGameTitle(String in) {
    return in.toLowerCase().replace(' ', '_');
  }

}
