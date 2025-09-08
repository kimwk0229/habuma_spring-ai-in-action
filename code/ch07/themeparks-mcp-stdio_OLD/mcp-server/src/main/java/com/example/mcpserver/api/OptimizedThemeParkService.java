package com.example.mcpserver.api;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OptimizedThemeParkService {

  public static final String THEME_PARKS_API_URL =
      "https://api.themeparks.wiki/v1";

  private final RestClient restClient;

  public OptimizedThemeParkService(
      RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder
        .baseUrl(THEME_PARKS_API_URL)
        .build();
  }

  // tag::getParksMethods[]
  @Tool(name = "getAllParks",
      description = "Get a list of all parks (including their name and " +
          "entity ID)")
  public List<Park> getAllParks() {    // <1>
    return getParkStream(park -> true)
        .collect(Collectors.toList());
  }

  @Tool(name = "getParksByName",
      description = "Get a list of parks (including their name and " +
          "entity ID) given a park name or resort name")
  public List<Park> getParksByName(String parkName) {       // <2>
    String lcParkName = parkName.toLowerCase();
    return getParkStream(
        park -> park.name().toLowerCase().contains(lcParkName)
            || park.resortName().toLowerCase().contains(lcParkName))
        .collect(Collectors.toList());
  }

  private Stream<Park> getParkStream(Predicate<Park> filter) {      // <3>
    DestinationList destinationList = restClient.get()
        .uri("/destinations")
        .retrieve()
        .body(DestinationList.class);

    return Objects.requireNonNull(destinationList).destinations.stream()
        .flatMap(destination -> destination.parks().stream()
            .map(park ->        // <4>
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
  // end::getParksMethods[]

  // unchanged from SimpleThemeParkService
  @Tool(name = "getEntity",
      description = "Get data for a park, attraction, or show given the " +
          "entity ID")
  public String getEntity(String entityId) {
    return sendRequestTo("/entity/{entityId}", entityId);
  }

  // unchanged from SimpleThemeParkService
  @Tool(name = "getEntityChildren",
      description = "Get a list of attractions and shows in a park " +
          "given the park's entity ID")
  public String getEntityChildren(String entityId) {
    return sendRequestTo("/entity/{entityId}/children", entityId);
  }

  // tag::getEntityScheduleForDate[]
  @Tool(name = "getEntityScheduleForDate",
      description = "Get a park's operating hours given the park's " +
          "entity ID and a specific date (in yyyy-MM-dd format).")
  public List<ScheduleEntry> getEntitySchedule(
      String entityId, String date) {
    String[] dateSplit = date.split("-");   // <1>
    String year = dateSplit[0];
    String month = dateSplit[1];

    Schedule schedule = restClient.get()
        .uri("/entity/{entityId}/schedule/{year}/{month}",   // <2>
            entityId, year, month)
        .retrieve()
        .body(Schedule.class);

    return schedule.schedule().stream()
        .filter(scheduleEntry -> scheduleEntry.date().equals(date)) // <3>
        .toList();
  }
  // end::getEntityScheduleForDate[]

  @Tool(name = "getEntityLive",
      description = "Get an attraction's wait times or a show's show " +
          "times given the attraction or show entity ID")
  public String getEntityLive(String entityId) {
    return sendRequestTo("/entity/{entityId}/live", entityId);
  }

  private String sendRequestTo(String path, Object... pathVariables) {
    return restClient
        .get()
        .uri(path, pathVariables)
        .retrieve()
        .body(String.class);
  }

  // tag::getParksMethodsTypes[]
  public record Park(
      String id,
      String name,
      String resortId,
      String resortName) {}

  private record DestinationList(
      List<Destination> destinations) {}

  private record Destination(
      String id,
      String name,
      List<Park> parks) {}
  // end::getParksMethodsTypes[]

  // tag::getEntityScheduleForDateTypes[]
  private record Schedule(
      String id,
      String name,
      List<ScheduleEntry> schedule) {}

  public record ScheduleEntry(
      String date,
      String type,
      String description,
      String openingTime,
      String closingTime) {}
  // end::getEntityScheduleForDateTypes[]

}
