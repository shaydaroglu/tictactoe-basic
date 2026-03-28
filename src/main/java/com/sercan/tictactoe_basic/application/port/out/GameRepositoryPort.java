package com.sercan.tictactoe_basic.application.port.out;

import com.sercan.tictactoe_basic.domain.model.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameRepositoryPort {
    Game save(Game game);
    Optional<Game> findById(UUID id);
}
