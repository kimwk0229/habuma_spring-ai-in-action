// tag::withTTS[]
// tag::initialVoiceService[]
package com.example.boardgamebuddy;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAiVoiceService implements VoiceService {

  private final OpenAiAudioTranscriptionModel transcriptionModel;
  // end::initialVoiceService[]
  private final SpeechModel speechModel;
  // tag::initialVoiceService[]

  // end::withTTS[]
  // end::initialVoiceService[]
  /*
  // tag::initialVoiceService[]
  public OpenAiVoiceService(
      OpenAiAudioTranscriptionModel transcriptionModel) {
    this.transcriptionModel = transcriptionModel; // <1>
  }
  // end::initialVoiceService[]
  */

// tag::withTTS[]
  public OpenAiVoiceService(
      OpenAiAudioTranscriptionModel transcriptionModel,
      SpeechModel speechModel) {
    this.transcriptionModel = transcriptionModel;
    this.speechModel = speechModel;
  }
  // end::withTTS[]

  /*
  // tag::withTTS[]

  ...

// end::withTTS[]
   */

  // tag::initialVoiceService[]

  @Override
  public String transcribe(Resource audioFileResource) {
    return transcriptionModel.call(audioFileResource); // <2>
  }

  // end::initialVoiceService[]

  /*

  // tag::initialVoiceService[]
  @Override
  public byte[] textToSpeech(String text) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  // end::initialVoiceService[]
  */

  // tag::textToSpeech[]
  @Override
  public Resource textToSpeech(String text) {
    byte[] speechBytes = speechModel.call(text);
    return new ByteArrayResource(speechBytes);
  }
  // end::textToSpeech[]

  // tag::initialVoiceService[]
// tag::withTTS[]
}
// end::withTTS[]
// end::initialVoiceService[]

