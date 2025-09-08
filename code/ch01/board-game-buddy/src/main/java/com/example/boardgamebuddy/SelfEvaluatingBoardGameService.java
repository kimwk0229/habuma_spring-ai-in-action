// tag::all[]
package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

// end::all[]
/*
// tag::all[]
@Service
// end::all[]
*/
// tag::all[]
public class SelfEvaluatingBoardGameService implements BoardGameService {

  private final ChatClient chatClient;
  private final RelevancyEvaluator evaluator;

  public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
    ChatOptions chatOptions = ChatOptions.builder()
        .model("gpt-4o-mini")
        .build();

    this.chatClient = chatClientBuilder
        .defaultOptions(chatOptions)
        .build();

    this.evaluator = new RelevancyEvaluator(chatClientBuilder); // <1>
  }

  @Override
  @Retryable(retryFor = AnswerNotRelevantException.class)  // <2>
  public Answer askQuestion(Question question) {
    String answerText = chatClient.prompt()
        .user(question.question())
        .call()
        .content();

    evaluateRelevancy(question, answerText);

    return new Answer(answerText);
  }

  @Recover // <3>
  public Answer recover(AnswerNotRelevantException e) {
    return new Answer("I'm sorry, I wasn't able to answer the question.");
  }

  private void evaluateRelevancy(Question question, String answerText) {
    EvaluationRequest evaluationRequest =
        new EvaluationRequest(question.question(), List.of(), answerText);
    EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
    if (!evaluationResponse.isPass()) {
      throw new AnswerNotRelevantException(question.question(), answerText); // <4>
    }
  }

}
// end::all[]
