package com.example.boardgamebuddy.gamedata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component("gameComplexityFunction")
@Description("Returns a game's complexity/difficulty " +
    "given the game's title/name.")
public class GameComplexityFunction
    implements Function<GameComplexityRequest, GameComplexityResponse> {

  private final Logger LOGGER =
      LoggerFactory.getLogger(GameComplexityFunction.class);

  private final GameRepository gameRepository;

  GameComplexityFunction(GameRepository gameRepository) { // <1>
    this.gameRepository = gameRepository;
  }

  @Override
  public GameComplexityResponse apply(GameComplexityRequest gameDataRequest) { // <2>

    String gameSlug = gameDataRequest.title()
        .toLowerCase()
        .replace(" ", "_");

    LOGGER.info("Getting complexity for {} ({})",
        gameDataRequest.title(), gameSlug);

    Optional<Game> gameOpt = gameRepository.findBySlug(gameSlug);

    Game game = gameOpt.orElseGet(() -> {
      LOGGER.warn("Game not found: {}", gameSlug);
      return new Game(
          null,
          gameSlug,
          gameDataRequest.title(),
          GameComplexity.UNKNOWN.getValue());
    });

    return new GameComplexityResponse(
        game.title(), game.complexityEnum());

  }
}
