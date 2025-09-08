package com.example.mcpserver.api;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SimpleThemeParkService {
  public static final String THEME_PARKS_API_URL =
      "https://api.themeparks.wiki/v1";

  private final RestClient restClient;

  public SimpleThemeParkService(
      RestClient.Builder restClientBuilder) {  // <1>
    this.restClient = restClientBuilder
        .baseUrl(THEME_PARKS_API_URL)
        .build();
  }

  @Tool(name = "getDestinations",
      description = "Get list of resort destinations, including their " +
          "entity ID, name, and a child list of theme parks")
  public String getDestinations() {
    return sendRequestTo("/destinations");   // <2>
  }

  @Tool(name = "getEntity",
      description = "Get data for a park, attraction, or show given the " +
          "entity ID")
  public String getEntity(String entityId) {
    return sendRequestTo("/entity/{entityId}", entityId);   // <2>
  }

  @Tool(name = "getEntityChildren",
      description = "Get a list of attractions and shows in a park " +
          "given the park's entity ID")
  public String getEntityChildren(String entityId) {
    return sendRequestTo("/entity/{entityId}/children", entityId);   // <2>
  }

  @Tool(name = "getEntitySchedule",
      description = "Get a park's operating hours given the park's " +
          "entity ID.")
  public String getEntitySchedule(String entityId) {
    return sendRequestTo("/entity/{entityId}/schedule", entityId);   // <2>
  }

  @Tool(name = "getEntityScheduleForDate",
      description = "Get a park's operating hours given the park's " +
          "entity ID and a specific year and month.")
  public String getEntitySchedule(
      String entityId, String year, String month) {
    return sendRequestTo("/entity/{entityId}/schedule/{year}/{month}",
        entityId, year, month);   // <2>
  }

  @Tool(name = "getEntityLive",
      description = "Get an attraction's wait times or a show's show " +
          "times given the attraction or show entity ID")
  public String getEntityLive(String entityId) {
    return sendRequestTo("/entity/{entityId}/live", entityId);   // <2>
  }

  private String sendRequestTo(String path, Object... pathVariables) {    // <3>
    return restClient
        .get()
        .uri(path, pathVariables)
        .retrieve()
        .body(String.class);
  }

}
