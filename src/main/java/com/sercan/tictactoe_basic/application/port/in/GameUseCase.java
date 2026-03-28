package com.sercan.tictactoe_basic.application.port.in;

import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Symbol;

public interface GameUseCase {
    Game createGame(Symbol playerOneSymbol);
}
