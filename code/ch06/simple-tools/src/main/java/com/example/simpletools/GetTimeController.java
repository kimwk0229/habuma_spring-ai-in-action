package com.example.simpletools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetTimeController {

  private static final String CURRENT_TIME_TEMPLATE =
      "What is the current time in {city}?";

  private final ChatClient chatClient;

  public GetTimeController(ChatClient.Builder chatClientBuilder,
                           TimeTools timeTools) {  // <1>
    this.chatClient = chatClientBuilder
        .defaultTools(timeTools)  // <2>
        .build();
  }

  @GetMapping(path="/time", params = "city")
  public String getTime(@RequestParam("city") String city) {
    return chatClient.prompt()
          .user(userSpec -> {
              userSpec
                  .text(CURRENT_TIME_TEMPLATE)
                  .param("city", city);
            })
          .call()
          .content();
  }

}
