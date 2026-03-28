package com.sercan.tictactoe_basic.application.port.in;

import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Symbol;

import java.util.UUID;

public interface GameUseCase {
    Game createGame(Symbol playerOneSymbol);
    Game getGame(UUID gameId);
}
