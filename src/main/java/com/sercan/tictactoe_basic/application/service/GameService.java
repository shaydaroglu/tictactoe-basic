package com.sercan.tictactoe_basic.application.service;

import com.sercan.tictactoe_basic.application.exception.GameNotFoundException;
import com.sercan.tictactoe_basic.application.port.in.GameUseCase;
import com.sercan.tictactoe_basic.application.port.out.GameRepositoryPort;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.sercan.tictactoe_basic.domain.model.GameStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
public class GameService implements GameUseCase {
    private final GameRepositoryPort gameRepositoryPort;

    public Game createGame(Symbol symbol) {
        Game newGame = Game.builder()
                .status(IN_PROGRESS)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(symbol)
                .moveCount(0)
                .build();

        return gameRepositoryPort.save(newGame);
    }

    public Game getGame(UUID gameId) {
        return gameRepositoryPort.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
    }
}
