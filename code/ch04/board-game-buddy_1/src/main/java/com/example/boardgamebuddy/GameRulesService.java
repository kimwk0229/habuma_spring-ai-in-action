package com.example.boardgamebuddy;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRulesService {

  private final VectorStore vectorStore;

  public GameRulesService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public String getRulesFor(String gameName, String question) {
    SearchRequest searchRequest = SearchRequest
        .builder()
        .query(question)
        .build();
//        .withFilterExpression(
//            new FilterExpressionBuilder()
//                .eq("gameTitle", normalizeGameTitle(gameName)).build()); // <1>

    System.err.println("Search request: " + searchRequest);

    List<Document> similarDocs =
        vectorStore.similaritySearch(searchRequest); // <2>

    if (similarDocs.isEmpty()) {
      return "The rules for " + gameName + " are not available.";
    }

    return similarDocs.stream()
        .map(Document::getText)
        .collect(Collectors.joining(System.lineSeparator())); // <3>
  }

  private String normalizeGameTitle(String gameTitle) {  // <4>
    return gameTitle.toLowerCase().replace(" ", "_");
  }

}
