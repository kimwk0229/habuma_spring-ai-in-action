package com.example.mcpclient;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.ClientMcpTransport;
import org.springframework.ai.mcp.McpToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

  @Bean(initMethod = "initialize")
  public McpSyncClient mcpClient(ClientMcpTransport transport) {
    return McpClient.sync(transport).build();
  }

  // tag::stdioTransport[]
  @Bean
  public ClientMcpTransport stdioTransport() {
    return new StdioClientTransport(ServerParameters.builder("java")
        .args("-jar", "../mcp-server/build/libs/mcp-server-0.0.1-SNAPSHOT.jar")
        .build());
  }
  // end::stdioTransport[]

  @Bean
  public McpToolCallback[] functionCallbacks(McpSyncClient mcpClient) {
    return mcpClient.listTools(null)
        .tools()
        .stream()
        .map(tool -> new McpToolCallback(mcpClient, tool))
        .toArray(McpToolCallback[]::new);
  }

}
