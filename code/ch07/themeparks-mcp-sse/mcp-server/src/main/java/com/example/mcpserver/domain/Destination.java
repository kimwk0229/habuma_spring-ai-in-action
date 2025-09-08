package com.example.mcpserver.domain;

import java.util.List;

public record Destination(
    String id,
    String name,
    List<Entity> parks) {}
