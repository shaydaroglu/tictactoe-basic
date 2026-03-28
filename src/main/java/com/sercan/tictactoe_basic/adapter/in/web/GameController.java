package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.adapter.in.web.dto.request.CreateGameRequestDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.request.MakeMoveRequestDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.response.GameResponseDto;
import com.sercan.tictactoe_basic.application.port.in.GameUseCase;
import com.sercan.tictactoe_basic.application.port.in.MoveUseCase;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Move;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameUseCase gameUseCase;
    private final MoveUseCase moveUseCase;
    private final GameResponseMapper gameResponseMapper;

    @PostMapping
    public GameResponseDto createGame(@Valid @RequestBody CreateGameRequestDto createGameRequestDto) {
        Game game = gameUseCase.createGame(createGameRequestDto.playerOneSymbol());

        return gameResponseMapper.mapNewGame(game);
    }

    @GetMapping("/{id}")
    public GameResponseDto getGame(@PathVariable UUID id) {
        Game game = gameUseCase.getGame(id);
        List<Move> moves = moveUseCase.getMoves(game.id());

        return gameResponseMapper.map(game, moves);
    }

    @PostMapping("/{id}/moves")
    public GameResponseDto makeMove(@PathVariable UUID id,
                                    @Valid @RequestBody MakeMoveRequestDto makeMoveRequestDto) {
        Game game = moveUseCase.makeMove(id, makeMoveRequestDto.player(), makeMoveRequestDto.position());
        List<Move> moves = moveUseCase.getMoves(game.id());

        return gameResponseMapper.map(game, moves);
    }
}
