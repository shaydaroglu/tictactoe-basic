package com.sercan.tictactoe_basic.application.exception;

public class GameAlreadyFinishedException extends RuntimeException {
    public GameAlreadyFinishedException() {
        super("Game is already finished");
    }
}
