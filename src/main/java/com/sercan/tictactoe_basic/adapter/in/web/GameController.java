package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.adapter.in.web.dto.request.CreateGameRequestDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.request.MakeMoveRequestDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.response.ErrorResponseDto;
import com.sercan.tictactoe_basic.adapter.in.web.dto.response.GameResponseDto;
import com.sercan.tictactoe_basic.application.port.in.GameUseCase;
import com.sercan.tictactoe_basic.application.port.in.MoveUseCase;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Move;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
@Tag(name = "Tic Tac Toe", description = "Endpoints for creating games, making moves, and reading game state")
public class GameController {
    private final GameUseCase gameUseCase;
    private final MoveUseCase moveUseCase;
    private final GameResponseMapper gameResponseMapper;

    @PostMapping
    @Operation(
            summary = "Create a new game",
            description = "Creates a new Tic Tac Toe game and assigns the requested symbol to player one."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Game created successfully",
                    content = @Content(schema = @Schema(implementation = GameResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or unsupported symbol",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Invalid Symbol",
                                    value = """
                                            {
                                              "timestamp": "2026-03-28T18:00:00Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "playerOneSymbol must be one of: X, O"
                                            }
                                            """
                            )
                    )
            )
    })
    public GameResponseDto createGame(@Valid @RequestBody CreateGameRequestDto createGameRequestDto) {
        Game game = gameUseCase.createGame(createGameRequestDto.playerOneSymbol());

        return gameResponseMapper.mapNewGame(game);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get game state",
            description = "Returns the current state of the game, including board contents, move count, and winner when applicable."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Game returned successfully",
                    content = @Content(schema = @Schema(implementation = GameResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public GameResponseDto getGame(@PathVariable UUID id) {
        Game game = gameUseCase.getGame(id);
        List<Move> moves = moveUseCase.getMoves(game.id());

        return gameResponseMapper.map(game, moves);
    }

    @PostMapping("/{id}/moves")
    @Operation(
            summary = "Make a move",
            description = "Applies a move for the requested player and returns the updated game state."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Move applied successfully",
                    content = @Content(schema = @Schema(implementation = GameResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid move or malformed request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Concurrent update conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public GameResponseDto makeMove(@PathVariable UUID id,
                                    @Valid @RequestBody MakeMoveRequestDto makeMoveRequestDto) {
        Game game = moveUseCase.makeMove(id, makeMoveRequestDto.player(), makeMoveRequestDto.position());
        List<Move> moves = moveUseCase.getMoves(game.id());

        return gameResponseMapper.map(game, moves);
    }
}
