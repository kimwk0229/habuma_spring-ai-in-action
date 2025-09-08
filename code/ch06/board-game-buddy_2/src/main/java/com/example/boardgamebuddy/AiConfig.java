package com.example.boardgamebuddy;

import com.example.boardgamebuddy.gamedata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.function.Function;

@Configuration
public class AiConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiConfig.class);

  // tag::chatClientBean_defTools[]
  @Bean
  ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                        VectorStore vectorStore,
                        GameTools gameTools) {
    return chatClientBuilder
        .defaultAdvisors(
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build(),
            VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
        .defaultTools("gameTools")
        .build();
  }
  // end::chatClientBean_defTools[]

}
