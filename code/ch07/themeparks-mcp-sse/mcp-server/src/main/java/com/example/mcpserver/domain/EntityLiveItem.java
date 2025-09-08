package com.example.mcpserver.domain;

import java.util.List;
import java.util.Map;

public record EntityLiveItem(
    Map<String, Object> queue,
    List<ShowTime> showtimes) {}
