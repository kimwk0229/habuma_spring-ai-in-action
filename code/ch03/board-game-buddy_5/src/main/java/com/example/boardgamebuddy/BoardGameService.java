package com.example.boardgamebuddy;

import reactor.core.publisher.Flux;

public interface BoardGameService {
    Flux<String> askQuestion(Question question);
}
