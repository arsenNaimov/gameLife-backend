package myCode.gameLife.controller;

import myCode.gameLife.entity.Game;
import myCode.gameLife.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gameLife")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public Game getOldGame() {
        return gameService.getOldGame();
    }

    @PostMapping
    public Game newGeneration(@RequestBody Game game) {
        return gameService.newGeneration(game);
    }
}
