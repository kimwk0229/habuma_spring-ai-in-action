package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class GameRulesService {

  private static final Logger LOG =
      LoggerFactory.getLogger(GameRulesService.class);

  public String getRulesFor(String gameName) {
    try {
      String filename = String.format(
          "classpath:/gameRules/%s.txt",
          gameName.toLowerCase().replace(" ", "_")); // <1>

      return new DefaultResourceLoader()
          .getResource(filename)
          .getContentAsString(Charset.defaultCharset()); // <2>
    } catch (IOException e) {
      LOG.info("No rules found for game: " + gameName);
      return "";                                            // <3>
    }
  }

}
