package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  // tag::chatClientBean[]
  @Bean
  ChatMemory chatMemory() {      // <1>
    return new InMemoryChatMemory();
  }

  @Bean
  ChatClient chatClient(
          ChatClient.Builder chatClientBuilder, 
          VectorStore vectorStore,
          ChatMemory chatMemory) {
    return chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build(),     // <2>
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build())
          .build();
  }
  // end::chatClientBean[]

}
