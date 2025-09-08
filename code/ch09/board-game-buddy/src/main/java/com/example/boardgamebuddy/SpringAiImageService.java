package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class SpringAiImageService implements ImageService {

  private static final Logger LOG =
      LoggerFactory.getLogger(SpringAiImageService.class);
  private final ImageModel imageModel;

  public SpringAiImageService(ImageModel imageModel) {
    this.imageModel = imageModel;
  }

  @Override
  public String generateImageForUrl(String instructions) {
    return generateImage(instructions, "url")
        .getResult()
        .getOutput()
        .getUrl();     // <1>
  }

  @Override
  public byte[] generateImageForImageBytes(String instructions) {
    String base64Image = generateImage(instructions, "b64_json")
        .getResult()
        .getOutput()
        .getB64Json();    // <2>
    return Base64.getDecoder().decode(base64Image); // <3>
  }

  private ImageResponse generateImage(String instructions, String format) {
    LOG.info("Image prompt instructions: {}", instructions);

    ImageOptions options = ImageOptionsBuilder.builder()
        .width(1024)
        .height(1024)
        .responseFormat(format)
        .build();  // <4>

    ImagePrompt imagePrompt =
        new ImagePrompt(instructions, options); // <5>

    return imageModel.call(imagePrompt); // <6>
  }
}
