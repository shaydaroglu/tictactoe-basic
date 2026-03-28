package com.sercan.tictactoe_basic.application.port.in;

import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Move;
import com.sercan.tictactoe_basic.domain.model.Player;

import java.util.List;
import java.util.UUID;

public interface MoveUseCase {
    Game makeMove(UUID gameId, Player player, int position);
    List<Move> getMoves(UUID gameId);
}
