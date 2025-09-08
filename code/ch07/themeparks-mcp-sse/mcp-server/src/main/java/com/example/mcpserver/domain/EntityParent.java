package com.example.mcpserver.domain;

import java.util.List;

public record EntityParent(
    String id,
    String name,
    List<Entity> children) {}
