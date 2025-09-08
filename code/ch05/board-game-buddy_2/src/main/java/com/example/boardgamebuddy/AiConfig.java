package com.example.boardgamebuddy;

import com.example.boardgamebuddy.chatmemory.ConversationRepository;
import com.example.boardgamebuddy.chatmemory.MongoChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  // tag::mongoChatMemory[]
  @Bean
  ChatMemory chatMemory(ConversationRepository conversationRepository) {
    return new MongoChatMemory(conversationRepository);
  }
  // end::mongoChatMemory[]

  @Bean
  ChatClient chatClient(
      ChatClient.Builder chatClientBuilder,
      VectorStore vectorStore,
      ChatMemory chatMemory) {
    return chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory)
                .chatMemoryRetrieveSize(3).build(),
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build())
        .build();
  }

}
