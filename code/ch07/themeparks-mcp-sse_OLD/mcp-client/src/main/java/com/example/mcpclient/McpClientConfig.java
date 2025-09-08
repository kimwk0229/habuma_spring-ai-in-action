package com.example.mcpclient;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.ClientMcpTransport;
import org.springframework.ai.mcp.McpToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

  @Bean(initMethod = "initialize")
  public McpSyncClient mcpClient(ClientMcpTransport transport) {
    return McpClient.sync(transport).build();
  }

  // tag::clientSseTransport[]
  @Bean
  public ClientMcpTransport sseTransport(
      @Value("${mcp.server.url}") String mcpServerUrl) {
    return new HttpClientSseClientTransport(mcpServerUrl);
  }
  // end::clientSseTransport[]

  @Bean
  public McpToolCallback[] functionCallbacks(McpSyncClient mcpClient) {
    return mcpClient.listTools(null)
        .tools()
        .stream()
        .map(tool -> new McpToolCallback(mcpClient, tool))
        .toArray(McpToolCallback[]::new);
  }

}
