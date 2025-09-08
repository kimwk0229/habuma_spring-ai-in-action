package com.example.boardgamebuddy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SpringAiBoardGameServiceTests {

  @Autowired
  private BoardGameService boardGameService;

  @Autowired
  private ChatClient.Builder chatClientBuilder;

  @Test
  public void evaluateRelevancy() {
    String userText = "How many pieces are there?";
    String game = "Checkers";
    Question question = new Question(game, userText);
    Answer answer = boardGameService.askQuestion(question);
    RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    String testQuestion = "In the game of checkers, " + userText;
    EvaluationResponse response = relevancyEvaluator.evaluate(new EvaluationRequest(testQuestion, List.of(), answer.answer()));
    Assertions.assertThat(response.isPass())
        .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, answer.answer(), testQuestion)
        .isTrue();
  }

}
