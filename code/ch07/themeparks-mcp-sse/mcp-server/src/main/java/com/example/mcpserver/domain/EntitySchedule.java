package com.example.mcpserver.domain;

import java.util.List;

public record EntitySchedule(
    String id,
    String name,
    List<EntityScheduleItem> schedule) {}
