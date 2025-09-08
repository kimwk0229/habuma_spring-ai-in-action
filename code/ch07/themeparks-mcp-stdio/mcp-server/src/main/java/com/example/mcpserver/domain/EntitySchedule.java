package com.example.mcpserver.domain;

import java.util.List;

// tag::mainRecord[]
public record EntitySchedule(
    String id,
    String name,
    List<EntityScheduleItem> schedule) {}
// end::mainRecord[]
