package com.sercan.tictactoe_basic.application.exception;

public class PositionAlreadyTakenException extends RuntimeException {
    public PositionAlreadyTakenException(int position) {
        super("Position '" + position + "' is already taken");
    }
}
