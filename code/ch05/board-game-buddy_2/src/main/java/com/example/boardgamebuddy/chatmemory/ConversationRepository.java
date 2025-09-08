package com.example.boardgamebuddy.chatmemory;

import org.springframework.data.repository.CrudRepository;

public interface ConversationRepository
         extends CrudRepository<Conversation, String> {
}
