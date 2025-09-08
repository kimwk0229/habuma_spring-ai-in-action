// tag::MongoChatMemory_shell[]
package com.example.boardgamebuddy.chatmemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoChatMemory implements ChatMemory { // <1>

  private final ConversationRepository conversationRepository;

  public MongoChatMemory(
        ConversationRepository conversationRepository) { // <2>
    this.conversationRepository = conversationRepository;
  }

// end::MongoChatMemory_shell[]

  /*
  // tag::MongoChatMemory_shell[]
  // ...
  // end::MongoChatMemory_shell[]
   */

  // tag::MongoChatMemory_add[]
  @Override
  public void add(String conversationId, List<Message> messages) {
    List<ConversationMessage> conversationMessages = messages.stream()
        .map(message -> new ConversationMessage(
            message.getMessageType().getValue(), message.getText()))
        .toList();                     // <1>

    conversationRepository.findById(conversationId)
        .ifPresentOrElse(conversation -> {
          List<ConversationMessage> existingMessages = conversation.messages();
          existingMessages.addAll(conversationMessages);
          conversationRepository.save(
              new Conversation(conversationId, existingMessages));  // <2>
        },
        () -> conversationRepository.save(
            new Conversation(conversationId, conversationMessages)));  // <3>
  }
  // end::MongoChatMemory_add[]

  // tag::MongoChatMemory_get[]
  @Override
  public List<Message> get(String conversationId, int lastN) {
    return conversationRepository
        .findById(conversationId)  // <1>
        .map(conversation -> {
          List<Message> messageList = conversation.messages().stream()
              .map(conversationMessage -> {
                String messageType = conversationMessage.messageType();
                Message message =
                        messageType.equals(MessageType.USER.getValue()) ?
                    new UserMessage(conversationMessage.content()) :
                    new AssistantMessage(conversationMessage.content());
                return message;
              }).toList();  // <2>

          return messageList.stream()
              .skip(Math.max(0, messageList.size() - lastN)) // <3>
              .toList();
        }).orElse(List.of()); // <4>
  }
  // end::MongoChatMemory_get[]

  // tag::MongoChatMemory_clear[]
  @Override
  public void clear(String conversationId) {
    conversationRepository.deleteById(conversationId);
  }
  // end::MongoChatMemory_clear[]

// tag::MongoChatMemory_shell[]

}
// end::MongoChatMemory_shell[]