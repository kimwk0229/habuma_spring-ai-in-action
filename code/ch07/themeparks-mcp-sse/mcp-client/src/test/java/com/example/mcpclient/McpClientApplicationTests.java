package com.example.mcpclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.ai.mcp.client.enabled=false")
class McpClientApplicationTests {

  @Test
  @Disabled("For now...until I can disable MCP client")
  void contextLoads() {
  }

}
