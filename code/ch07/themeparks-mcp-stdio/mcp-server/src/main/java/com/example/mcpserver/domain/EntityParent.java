package com.example.mcpserver.domain;

import java.util.List;

// tag::mainRecord[]
public record EntityParent(
    String id,
    String name,
    List<Entity> children) {}
// end::mainRecord[]
