package com.sercan.tictactoe_basic.adapter.in.web.dto.response;

import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Current game state returned by the API")
public record GameResponseDto(
        @Schema(description = "Unique identifier of the game", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID gameId,
        @Schema(description = "Current game status", example = "IN_PROGRESS")
        GameStatus status,
        @Schema(description = "Player whose turn it is when the game is still in progress", example = "PLAYER_2")
        Player currentPlayer,
        @Schema(description = "Symbol chosen by player one", example = "X")
        Symbol playerOneSymbol,
        @Schema(description = "Winner of the game when one exists", example = "PLAYER_1", nullable = true)
        Player winner,
        @Schema(description = "Total number of moves played", example = "5")
        int moveCount,
        @ArraySchema(
                schema = @Schema(
                        description = "3x3 board matrix containing X, O, empty strings, or initial index hints",
                        example = "[[\"X\",\"X\",\"X\"],[\"O\",\"O\",\"\"],[\"\",\"\",\"\"]]"
                )
        )
        List<List<String>> board
) {
}
