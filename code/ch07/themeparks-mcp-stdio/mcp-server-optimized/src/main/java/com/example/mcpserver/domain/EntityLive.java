package com.example.mcpserver.domain;

import java.util.List;

// tag::mainRecord[]
public record EntityLive(
    String id,
    String name,
    List<EntityLiveItem> liveData) {}
// end::mainRecord[]
