package com.example.mcpserver;

import static com.example.mcpserver.api.OptimizedThemeParkService.Park;

import com.example.mcpserver.api.OptimizedThemeParkService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(OptimizedThemeParkService.class)
public class OptimizedThemeParkServiceTests {

  @Autowired
  private OptimizedThemeParkService themeParkService;

  @Autowired
  private MockRestServiceServer server;

  @Test
  public void testGetAllParks() throws Exception {
    List<Park> expectedAllParks = readParksJson("allParks.json");

    this.server.expect(requestTo("https://api.themeparks.wiki/v1/destinations"))
        .andRespond(withSuccess(new ClassPathResource("destinations.json"), MediaType.APPLICATION_JSON));

    List<Park> allParks = themeParkService.getAllParks();

    Assertions.assertThat(allParks).isEqualTo(expectedAllParks);
  }

  @Test
  public void testGetParksByName() throws Exception {
    List<Park> expectedParks = readParksJson("parksByName.json");
    this.server.expect(requestTo("https://api.themeparks.wiki/v1/destinations"))
        .andRespond(withSuccess(new ClassPathResource("destinations.json"), MediaType.APPLICATION_JSON));

    List<Park> parksByName = themeParkService.getParksByName("Disneyland");
    Assertions.assertThat(parksByName).isEqualTo(expectedParks);
  }

  private static List<Park> readParksJson(String fileName) throws JsonProcessingException, IOException {
    String expectedAllParksString = StreamUtils.copyToString(new ClassPathResource(fileName).getInputStream(), Charset.defaultCharset());
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(expectedAllParksString, objectMapper.getTypeFactory().constructCollectionType(List.class, Park.class));
  }

}
