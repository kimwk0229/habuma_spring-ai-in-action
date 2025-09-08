package com.example.boardgamebuddy;

import com.example.boardgamebuddy.gamedata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameTools {

  private final GameRepository gameRepository;

  public GameTools(GameRepository gameRepository) {  // <1>
    this.gameRepository = gameRepository;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(GameTools.class);

  @Tool(name = "getGameComplexity",
      description = "Returns a game's complexity/difficulty " +
          "given the game's title/name.")
  public GameComplexityResponse getGameComplexity(
            @ToolParam(description="The title of the game") // <2>
            String gameTitle) {
    String gameSlug = gameTitle    // <3>
        .toLowerCase()
        .replace(" ", "_");

    LOGGER.info("Getting complexity for {} ({})",
        gameTitle, gameSlug);

    Optional<Game> gameOpt = gameRepository.findBySlug(gameSlug);  // <4>

    Game game = gameOpt.orElseGet(() -> {  // <5>
      LOGGER.warn("Game not found: {}", gameSlug);
      return new Game(
          null,
          gameSlug,
          gameTitle,
          GameComplexity.UNKNOWN.getValue());
    });

    return new GameComplexityResponse(  // <6>
        game.title(), game.complexityEnum());
  }

}
