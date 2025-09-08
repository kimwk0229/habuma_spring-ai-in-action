package com.example.topsongs;

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
public class TopSongsRelevancyTest {

  @Autowired
  private TopSongsController topSongsController;

  @Autowired
  private ChatClient.Builder chatClientBuilder;

  @Test
  public void evaluateRelevancy() {
    List<String> results = topSongsController.topSongs("1984");
    RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);

    String userText = "What were the top 10 songs on the Billboard Hot 100 in 1984?";

    String resultsString = String.join("\n", results);


    EvaluationResponse response = relevancyEvaluator.evaluate(new EvaluationRequest(userText, List.of(), resultsString));
    Assertions.assertThat(response.isPass())
        .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, resultsString, userText)
        .isTrue();
  }

}
