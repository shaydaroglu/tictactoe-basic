package com.sercan.tictactoe_basic.domain.model;

import lombok.Builder;

import java.util.UUID;

import static com.sercan.tictactoe_basic.domain.model.Player.PLAYER_1;
import static com.sercan.tictactoe_basic.domain.model.Player.PLAYER_2;

@Builder
public record Game(UUID id, GameStatus status, Player currentPlayer, Symbol player1Symbol, Player winner,
                   int moveCount, Long version) {
    public boolean isFinished() {
        return status != GameStatus.IN_PROGRESS;
    }

    public Symbol getSymbolOf(Player player) {
        if (player == PLAYER_1) {
            return player1Symbol;
        }
        return player1Symbol == Symbol.X ? Symbol.O : Symbol.X;
    }

    public Player nextPlayer() {
        return currentPlayer == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }
}
