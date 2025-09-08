package com.example.boardgamebuddy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.qdrant.QdrantContainer;

import java.util.List;

@Testcontainers
@SpringBootTest(properties = "spring.ai.vectorstore.qdrant.initialize-schema=true")
public class SpringAiBoardGameServiceTests {

  @Container
  @ServiceConnection
  static QdrantContainer qdrant = new QdrantContainer("qdrant/qdrant:latest");

  @Autowired
  private BoardGameService boardGameService;

  @Autowired
  private ChatClient.Builder chatClientBuilder;

  @Autowired
  VectorStore vectorStore;

  @BeforeEach
  public void addTestDocs() {
    Document document = Document.builder()
        .text("There are 24 pieces in checkers, 12 for each player.")
        .metadata("gameTitle", "checkers")
        .build();
    vectorStore.add(List.of(document));
  }

  @Test
  public void evaluateRelevancy() {
    String userText = "How many pieces are there?";
    String game = "Checkers";
    Question question = new Question(game, userText);
    Answer answer = boardGameService.askQuestion(question, "conversationId");
    RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    EvaluationResponse response = relevancyEvaluator.evaluate(new EvaluationRequest(userText, List.of(), answer.answer()));
    Assertions.assertThat(response.isPass())
        .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, answer.answer(), userText)
        .isTrue();
  }

}
