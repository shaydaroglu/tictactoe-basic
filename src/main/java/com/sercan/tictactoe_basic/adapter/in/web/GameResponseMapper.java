package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.adapter.in.web.dto.response.GameResponseDto;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Move;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameResponseMapper {

    public GameResponseDto map(Game game, List<Move> moves) {
        return new GameResponseDto(
                game.id(),
                game.status(),
                game.currentPlayer(),
                game.player1Symbol(),
                game.winner(),
                game.moveCount(),
                buildBoard(game, moves)
        );
    }

    public GameResponseDto mapNewGame(Game game) {
        return new GameResponseDto(
                game.id(),
                game.status(),
                game.currentPlayer(),
                game.player1Symbol(),
                game.winner(),
                game.moveCount(),
                buildExampleBoard()
        );
    }

    public static List<List<String>> buildExampleBoard() {
        List<List<String>> board = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            board.add(new ArrayList<>(List.of("" + (i * 3), "" + (i * 3 + 1), "" + (i * 3 + 2))));
        }

        return board;
    }

    private static List<List<String>> buildBoard(Game game, List<Move> moves) {
        List<List<String>> board = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            board.add(new ArrayList<>(List.of("", "", "")));
        }

        for (Move move : moves) {
            int row = move.position() / 3;
            int col = move.position() % 3;
            board.get(row).set(col, game.getSymbolOf(move.player()).name());
        }

        return board;
    }
}
