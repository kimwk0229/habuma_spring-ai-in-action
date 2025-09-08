package com.example.mcpserver;

import com.example.mcpserver.api.SimpleThemeParkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.ServerMcpTransport;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;

@Configuration
public class McpServerConfig {

  public final SimpleThemeParkService themeParkService;

  public McpServerConfig(SimpleThemeParkService themeParkService) {
    this.themeParkService = themeParkService;
  }

  // tag::sseTransport[]
  @Bean
  HttpServletSseServerTransport httpServletSseServerTransport() {
    return new HttpServletSseServerTransport(new ObjectMapper(), "/mcp/message");
  }
  // end::sseTransport[]

  /*
  // tag::sseWebFluxTransport[]
  @Bean
  WebFluxSseServerTransport webFluxSseServerTransport() {
    return new WebFluxSseServerTransport(new ObjectMapper(), "/mcp/message");
  }
  // end::sseWebFluxTransport[]
  */

  @Bean
  public ServletRegistrationBean customServletBean(HttpServletSseServerTransport servlet) {
    return new ServletRegistrationBean(servlet);
  }

  @Bean
  public McpSyncServer mcpServer(ServerMcpTransport transport) {
    var capabilities = McpSchema.ServerCapabilities.builder()
        .tools(true)
        .build();

    ToolCallback[] toolCallbacks = MethodToolCallbackProvider
        .builder()
        .toolObjects(themeParkService)
        .build()
        .getToolCallbacks();

    return McpServer.sync(transport)
        .serverInfo("Theme Park MCP Server", "1.0.0")
        .capabilities(capabilities)
        .tools(McpToolUtils.toSyncToolRegistration(toolCallbacks))
        .build();
  }

}
