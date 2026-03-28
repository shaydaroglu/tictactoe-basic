package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.adapter.in.web.dto.request.CreateGameRequestDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.response.CreateGameResponse;
import com.sercan.tictactoe_basic.application.port.in.GameUseCase;
import com.sercan.tictactoe_basic.domain.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameUseCase gameUseCase;

    @PostMapping
    public CreateGameResponse createGame(@RequestBody CreateGameRequestDto createGameRequestDto) {
        Game game = gameUseCase.createGame(createGameRequestDto.playerOneSymbol());

        return new CreateGameResponse(
                game.id(),
                game.status(),
                game.currentPlayer(),
                game.player1Symbol(),
                game.moveCount()
        );
    }
}
