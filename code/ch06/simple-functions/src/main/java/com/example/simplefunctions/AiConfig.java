package com.example.simplefunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

@Configuration
public class AiConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiConfig.class);

  @Bean
  @Description("Gets the current time in a given city.")
  public Function<GetTimeRequest, GetTimeResponse> getCurrentTime() {
    return (request) -> {
      LOGGER.info("Getting the current time in {}", request.timeZone());
      LocalDateTime now = LocalDateTime.now(ZoneId.of(request.timeZone()));
      return new GetTimeResponse(now.toString());
    };
  }

  public record GetTimeRequest(String timeZone) {}

  public record GetTimeResponse(String currentTime) {}

}
