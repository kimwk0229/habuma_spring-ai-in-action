package com.example.simplefunctions;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GetTimeControllerTests {

  @Autowired
  private GetTimeController getTimeController;

  @Autowired
  private ChatClient.Builder chatClientBuilder;

  // RelevancyEvaluator Note:
  //
  // When the user text (question) is something that the LLM can normally answer, the
  // evaluation should pass. But if it is something that the LLM cannot answer without
  // tool help, the evaluation always fails unless the ChatClient.Builder is outfitted
  // with a function that would enable the LLM to answer the question.

  @Test
  public void evaluateRelevancy() {
    String time = getTimeController.getTime("Dallas");
    chatClientBuilder.defaultFunctions("getCurrentTime");
    RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    String userText = "What is the current time in Dallas?";
    EvaluationResponse response = relevancyEvaluator.evaluate(
            new EvaluationRequest(userText, List.of(), time));
    Assertions.assertThat(response.isPass())
        .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, time, userText)
        .isTrue();

  }

}
