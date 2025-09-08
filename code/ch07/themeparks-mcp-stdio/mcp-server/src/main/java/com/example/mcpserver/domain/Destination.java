package com.example.mcpserver.domain;

import java.util.List;

// tag::mainRecord[]
public record Destination(
    String id,
    String name,
    List<Entity> parks) {}
// end::mainRecord[]
