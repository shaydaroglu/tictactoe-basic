package com.sercan.tictactoe_basic.adapter.in.web.dto.response;

import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;

import java.util.UUID;

public record CreateGameResponse(UUID gameId, GameStatus status, Player currentPlayer, Symbol playerOneSymbol,
                                 int moveCount) {
}
