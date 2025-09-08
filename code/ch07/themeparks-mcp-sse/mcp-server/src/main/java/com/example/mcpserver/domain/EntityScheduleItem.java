package com.example.mcpserver.domain;

public record EntityScheduleItem(
    String date,
    String type,
    String description,
    String openingTime,
    String closingTime) {}
