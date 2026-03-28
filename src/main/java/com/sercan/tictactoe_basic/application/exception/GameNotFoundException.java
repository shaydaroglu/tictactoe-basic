package com.sercan.tictactoe_basic.application.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super("Game not found with id: '" + gameId + "'");
    }
}
