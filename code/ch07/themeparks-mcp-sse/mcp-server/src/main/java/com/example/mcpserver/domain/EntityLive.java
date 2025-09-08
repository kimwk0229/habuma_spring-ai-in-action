package com.example.mcpserver.domain;

import java.util.List;

public record EntityLive(
    String id,
    String name,
    List<EntityLiveItem> liveData) {}
