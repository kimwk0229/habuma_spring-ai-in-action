package com.example.boardgamebuddy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BurgerBattleArtController {

  private final BoardGameService boardGameService;
  private final ImageService imageService;

  public BurgerBattleArtController(BoardGameService boardGameService,
                                   ImageService imageService) {  // <1>
    this.boardGameService = boardGameService;
    this.imageService = imageService;
  }

  @GetMapping(path="/burgerBattleArt")
  public String burgerBattleArt(@RequestParam("burger") String burger) {
    String instructions = getImageInstructions(burger);
    return imageService.generateImageForUrl(instructions); // <2>
  }

  @GetMapping(path="/burgerBattleArt", produces = "image/png")
    public byte[] burgerBattleArtImage(@RequestParam("burger") String burger) {
    String instructions = getImageInstructions(burger);
    return imageService.generateImageForImageBytes(instructions); // <3>
  }

  private String getImageInstructions(String burger) {
    Question question = new Question(
        "Burger Battle",
        "What ingredients are on the " + burger + " burger?");
    Answer answer = boardGameService.askQuestion(
        question, "art_conversation");        // <4>

    return "A burger called " + burger + " " +
        "with the following ingredients: " + answer.answer() + ". " +
        "Style the background to match the name of the burger."; // <5>
  }

}
