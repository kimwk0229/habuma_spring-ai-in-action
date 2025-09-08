package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  // tag::chatClientBean[]
  @Bean
  ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
    return chatClientBuilder
        .defaultAdvisors(
            VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),    // <1>
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build())
        .build();
  }
  // end::chatClientBean[]

}
