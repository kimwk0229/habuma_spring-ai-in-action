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
import org.springframework.context.annotation.Description;

import java.util.Optional;
import java.util.function.Function;

@Configuration
public class AiConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiConfig.class);

  // tag::defaultFunctions[]
  @Bean
  ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {

    return chatClientBuilder
        .defaultAdvisors(
            VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build())
        .defaultTools("gameComplexityFunction")
        .build();
  }
  // end::defaultFunctions[]

//  @Bean
  // tag::gameData_functionBean[]
  @Description("Returns a game's complexity/difficulty " +
      "given the game's title/name.")
  Function<GameComplexityRequest, GameComplexityResponse>
      gameComplexityFunction(GameRepository gameRepository) {

    return gameDataRequest -> {
      String gameSlug = gameDataRequest.title()
          .toLowerCase()
          .replace(" ", "_"); // <1>

      LOGGER.info("Getting complexity for {} ({})",
          gameDataRequest.title(), gameSlug);

      Optional<Game> gameOpt = gameRepository.findBySlug(gameSlug); // <2>

      Game game = gameOpt.orElseGet(() -> {
        LOGGER.warn("Game not found: {}", gameSlug);
        return new Game(
            null,
            gameSlug,
            gameDataRequest.title(),
            GameComplexity.UNKNOWN.getValue());
      }); // <3>

      return new GameComplexityResponse(
          game.title(), game.complexityEnum());   // <4>
    };

  }
  // end::gameData_functionBean[]

}
