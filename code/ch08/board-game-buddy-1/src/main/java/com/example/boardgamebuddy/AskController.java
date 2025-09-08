// tag::injectTranscriptionService[]
package com.example.boardgamebuddy;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AskController {

  private final BoardGameService boardGameService;
  private final VoiceService voiceService;

  public AskController(BoardGameService boardGameService,
                       VoiceService voiceService) {  // <1>
    this.boardGameService = boardGameService;
    this.voiceService = voiceService;
  }
  // end::injectTranscriptionService[]

  /*
    // tag::injectTranscriptionService[]

  // ...

    // end::injectTranscriptionService[]
   */


  @PostMapping(path = "/ask", produces = "application/json")
  public Answer ask(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestBody Question question) {
    return boardGameService.askQuestion(question, conversationId);
  }

  // tag::audioAsk[]
  @PostMapping(path="/audioAsk", produces = "application/json") // <1>
  public Answer audioAsk(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestParam("audio") MultipartFile audioBlob,          // <2>
      @RequestParam("gameTitle") String gameTitle) {           // <3>

    String transcription = voiceService.transcribe(audioBlob.getResource());
    Question transcribedQuestion = new Question(gameTitle, transcription);
    return boardGameService.askQuestion(transcribedQuestion, conversationId);
  }
  // end::audioAsk[]

  // tag::textToSpeechBytes[]
  @PostMapping(path="/audioAsk", produces = "audio/mpeg")
  public Resource audioAskAudioResponse(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestParam("audio") MultipartFile blob,
      @RequestParam("gameTitle") String game) {

    String transcription = voiceService.transcribe(blob.getResource());
    Question transcribedQuestion = new Question(game, transcription);
    Answer answer = boardGameService.askQuestion(
            transcribedQuestion, conversationId);
    return voiceService.textToSpeech(answer.answer());
  }
  // end::textToSpeechBytes[]


  // tag::injectTranscriptionService[]
}
// end::injectTranscriptionService[]
