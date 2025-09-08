package com.example.mcpserver;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpServerConfig {

  @Bean
  List<ToolCallback> toolCallbacks(ThemeParkApiTools tpTools) {
    return List.of(ToolCallbacks.from(tpTools));
  }

}
