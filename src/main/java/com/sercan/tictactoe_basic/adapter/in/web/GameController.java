package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.adapter.in.web.dto.request.CreateGameRequestDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.response.GameResponseDto;
import com.sercan.tictactoe_basic.application.port.in.GameUseCase;
import com.sercan.tictactoe_basic.domain.model.Game;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameUseCase gameUseCase;

    @PostMapping
    public GameResponseDto createGame(@Valid @RequestBody CreateGameRequestDto createGameRequestDto) {
        Game game = gameUseCase.createGame(createGameRequestDto.playerOneSymbol());

        return new GameResponseDto(
                game.id(),
                game.status(),
                game.currentPlayer(),
                game.player1Symbol(),
                game.moveCount()
        );
    }

    @GetMapping("/{id}")
    public GameResponseDto getGame(@Valid @PathVariable UUID id) {
        Game game = gameUseCase.getGame(id);
        return new GameResponseDto(
                game.id(),
                game.status(),
                game.currentPlayer(),
                game.player1Symbol(),
                game.moveCount()
        );
    }
}
