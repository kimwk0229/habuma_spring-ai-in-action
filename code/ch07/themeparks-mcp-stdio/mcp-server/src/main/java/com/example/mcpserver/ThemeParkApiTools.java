package com.example.mcpserver;

import com.example.mcpserver.domain.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ThemeParkApiTools {

  private static final String THEMEPARKS_API_BASE_URL =
      "https://api.themeparks.wiki/v1";

  private final RestClient restClient;

  public ThemeParkApiTools(RestClient.Builder restClientBuilder) { // <1>
    this.restClient = restClientBuilder
        .baseUrl(THEMEPARKS_API_BASE_URL)
        .build();
  }

  @Tool(name = "getDestinations",
      description = "Get list of resort destinations, including their " +
          "entity ID, name, and a child list of theme parks")
  public List<Destination> getDestinations() {  // <2>
    return sendRequestTo("/destinations", DestinationList.class)
        .destinations();
  }

  @Tool(name = "getEntity",
      description = "Get data for a park, attraction, or show given the " +
          "entity ID")
  public Entity getEntity(String entityId) {  // <2>
    return sendRequestTo("/entity/{entityId}", Entity.class, entityId);
  }

  @Tool(name = "getEntityChildren",
      description = "Get a list of attractions and shows in a park " +
          "given the park's entity ID")
  public EntityParent getEntityChildren(String entityId) {  // <2>
    return sendRequestTo("/entity/{entityId}/children", EntityParent.class, entityId);
  }

  @Tool(name = "getEntitySchedule",
      description = "Get a park's operating hours given the park's " +
          "entity ID.")
  public EntitySchedule getEntitySchedule(String entityId) {  // <2>
    return sendRequestTo("/entity/{entityId}/schedule", EntitySchedule.class, entityId);
  }

  @Tool(name = "getEntityScheduleForDate",
      description = "Get a park's operating hours given the park's " +
          "entity ID and a specific year and month.")
  public EntitySchedule getEntitySchedule(
      String entityId, String year, String month) {  // <2>
    return sendRequestTo("/entity/{entityId}/schedule/{year}/{month}",
        EntitySchedule.class, entityId, year, month);
  }

  @Tool(name = "getEntityLive",
      description = "Get an attraction's wait times or a show's show " +
          "times given the attraction or show entity ID")
  public EntityLive getEntityLive(String entityId) {  // <2>
    return sendRequestTo("/entity/{entityId}/live", EntityLive.class, entityId);
  }

  private <T> T sendRequestTo(String uri,
                              Class<T> responseType,
                              Object... uriVariables) {   // <3>
    return restClient.get()
        .uri(uri, uriVariables)
        .retrieve()
        .body(responseType);
  }

}
