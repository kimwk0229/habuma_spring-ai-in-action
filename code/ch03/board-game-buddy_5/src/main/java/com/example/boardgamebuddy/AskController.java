package com.example.boardgamebuddy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AskController {

  private final BoardGameService boardGameService;

  public AskController(BoardGameService boardGameService) {
    this.boardGameService = boardGameService;
  }

  @PostMapping(path="/ask", produces="application/json") // <1>
  public Flux<String> ask(@RequestBody Question question) {
    return boardGameService.askQuestion(question);
  }

}
