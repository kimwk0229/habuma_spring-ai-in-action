package com.example.mcpserver.domain;

// tag::mainRecord[]
public record EntityScheduleItem(
    String date,
    String type,
    String description,
    String openingTime,
    String closingTime) {}
// end::mainRecord[]
