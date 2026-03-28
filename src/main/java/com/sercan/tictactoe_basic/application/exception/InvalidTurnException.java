package com.sercan.tictactoe_basic.application.exception;

import com.sercan.tictactoe_basic.domain.model.Player;

public class InvalidTurnException extends RuntimeException {
    public InvalidTurnException(Player player) {
        super("It is not " + player.name() + "'s turn");
    }
}
