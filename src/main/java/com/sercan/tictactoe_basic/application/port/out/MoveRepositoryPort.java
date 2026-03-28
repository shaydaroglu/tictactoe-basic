package com.sercan.tictactoe_basic.application.port.out;

import com.sercan.tictactoe_basic.domain.model.Move;

import java.util.List;
import java.util.UUID;

public interface MoveRepositoryPort {
    Move save(Move move);
    List<Move> findAllByGameIdOrderByMoveNumberAsc(UUID gameId);
    boolean existsByGameIdAndPosition(UUID gameId, int position);
}
