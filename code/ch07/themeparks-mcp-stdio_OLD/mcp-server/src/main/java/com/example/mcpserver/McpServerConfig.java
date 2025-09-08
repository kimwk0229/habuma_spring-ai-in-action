package com.example.mcpserver;

import com.example.mcpserver.api.SimpleThemeParkService;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.ServerMcpTransport;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

  public final SimpleThemeParkService themeParkService;

  public McpServerConfig(SimpleThemeParkService themeParkService) { // <1>
    this.themeParkService = themeParkService;
  }

  @Bean
  public StdioServerTransport stdioTransport() {  // <2>
    return new StdioServerTransport();
  }

  @Bean
  public McpSyncServer mcpServer(ServerMcpTransport transport) {
    var capabilities = McpSchema.ServerCapabilities.builder()
        .tools(true)                // <3>
        .build();

    ToolCallback[] toolCallbacks = MethodToolCallbackProvider   // <4>
        .builder()
        .toolObjects(themeParkService)
        .build()
        .getToolCallbacks();

    return McpServer.sync(transport)         // <5>
        .serverInfo("Theme Park MCP Server", "1.0.0")
        .capabilities(capabilities)
        .tools(McpToolUtils.toSyncToolRegistration(toolCallbacks))
        .build();
  }

}
