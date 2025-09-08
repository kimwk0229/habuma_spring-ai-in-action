package com.example.mcpserver;

import com.example.mcpserver.domain.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ThemeParkApiTools {

  private static final String THEMEPARKS_API_BASE_URL =
      "https://api.themeparks.wiki/v1";

  private final RestClient restClient;

  public ThemeParkApiTools(RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder
        .baseUrl(THEMEPARKS_API_BASE_URL)
        .build();
  }

  @Tool(name = "getAllParks",
      description = "Get a list of all parks (including their name and " +
          "entity ID)")
  public List<Park> getAllParks() {
    return getParkStream(park -> true)
        .collect(Collectors.toList());
  }

  @Tool(name = "getParksByName",
      description = "Get a list of parks (including their name and " +
          "entity ID) given a park name or resort name")
  public List<Park> getParksByName(String parkName) {
    String lcParkName = parkName.toLowerCase();
    return getParkStream(
        park -> park.name().toLowerCase().contains(lcParkName)
            || park.resortName().toLowerCase().contains(lcParkName))
        .collect(Collectors.toList());
  }

  private Stream<Park> getParkStream(Predicate<Park> filter) {
    var destinationList = sendRequestTo("/destinations", DestinationList.class);

    return destinationList.destinations().stream()
        .flatMap(destination -> destination.parks().stream()
            .map(park ->
                new Park(
                    park.id(),
                    stripNonAlphanumeric(park.name()),
                    destination.id(),
                    stripNonAlphanumeric(destination.name()))
            )
            .filter(filter));
  }

  private String stripNonAlphanumeric(String input) {
    return input.replaceAll("[^a-zA-Z0-9\\s-]", "");
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
  public EntityParent getEntityChildren(String entityId) {
    return sendRequestTo("/entity/{entityId}/children", EntityParent.class, entityId);
  }

  @Tool(name = "getEntityScheduleForDate",
      description = "Get a park's operating hours given the park's " +
          "entity ID and a specific date (in yyyy-MM-dd format).")
  public EntitySchedule getEntitySchedule(String entityId, String date) {
    String[] dateSplit = date.split("-");
    String year = dateSplit[0];
    String month = dateSplit[1];

    var fullSchedule = sendRequestTo("/entity/{entityId}/schedule/{year}/{month}",
        EntitySchedule.class, entityId, year, month);

    var filteredSchedule = fullSchedule.schedule().stream()
        .filter(scheduleEntry -> scheduleEntry.date().equals(date))
        .toList();

    return new EntitySchedule(
        fullSchedule.id(),
        fullSchedule.name(),
        filteredSchedule);
  }

  @Tool(name = "getEntityLive",
      description = "Get an attraction's wait times or a show's show " +
          "times given the attraction or show entity ID")
  public EntityLive getEntityLive(String entityId) {
    return sendRequestTo("/entity/{entityId}/live", EntityLive.class, entityId);
  }

  private <T> T sendRequestTo(String uri,
                              Class<T> responseType,
                              Object... uriVariables) {
    return restClient.get()
        .uri(uri, uriVariables)
        .retrieve()
        .body(responseType);
  }

}
