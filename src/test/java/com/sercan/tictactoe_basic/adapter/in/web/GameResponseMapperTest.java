package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.common.TestLoggerExtension;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Move;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TestLoggerExtension.class)
@DisplayName("Game Response Mapper Unit Tests")
class GameResponseMapperTest {

    private final GameResponseMapper mapper = new GameResponseMapper();

    @Test
    @DisplayName("maps board symbols correctly when player one chooses O")
    void mapUsesCorrectSymbolsWhenPlayerOneChoosesO() {
        UUID gameId = UUID.randomUUID();
        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.IN_PROGRESS)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(Symbol.O)
                .moveCount(2)
                .version(1L)
                .build();
        List<Move> moves = List.of(
                Move.builder().id(1L).gameId(gameId).position(0).player(Player.PLAYER_1).moveNumber(1).build(),
                Move.builder().id(2L).gameId(gameId).position(4).player(Player.PLAYER_2).moveNumber(2).build()
        );

        var response = mapper.map(game, moves);

        assertEquals("O", response.board().get(0).getFirst());
        assertEquals("X", response.board().get(1).get(1));
        assertEquals("", response.board().get(2).get(2));
    }
}
