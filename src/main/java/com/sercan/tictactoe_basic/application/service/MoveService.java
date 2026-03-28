package com.sercan.tictactoe_basic.application.service;

import com.sercan.tictactoe_basic.application.exception.GameAlreadyFinishedException;
import com.sercan.tictactoe_basic.application.exception.GameNotFoundException;
import com.sercan.tictactoe_basic.application.exception.InvalidTurnException;
import com.sercan.tictactoe_basic.application.exception.PositionAlreadyTakenException;
import com.sercan.tictactoe_basic.application.port.in.MoveUseCase;
import com.sercan.tictactoe_basic.application.port.out.GameRepositoryPort;
import com.sercan.tictactoe_basic.application.port.out.MoveRepositoryPort;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Move;
import com.sercan.tictactoe_basic.domain.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoveService implements MoveUseCase {

    private static final List<Set<Integer>> WINNING_COMBINATIONS = List.of(
            Set.of(0, 1, 2),
            Set.of(3, 4, 5),
            Set.of(6, 7, 8),
            Set.of(0, 3, 6),
            Set.of(1, 4, 7),
            Set.of(2, 5, 8),
            Set.of(0, 4, 8),
            Set.of(2, 4, 6)
    );

    private final GameRepositoryPort gameRepositoryPort;
    private final MoveRepositoryPort moveRepositoryPort;


    @Override
    @Transactional
    public Game makeMove(UUID gameId, Player player, int position) {
        Game game = gameRepositoryPort.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        validateMove(game, player, position);

        Move move = Move.builder()
                .gameId(gameId)
                .position(position)
                .player(player)
                .moveNumber(game.moveCount() + 1)
                .build();

        moveRepositoryPort.save(move);
        List<Move> moves = moveRepositoryPort.findAllByGameIdOrderByMoveNumberAsc(gameId);
        boolean isWinner = evalWinner(moves, player);
        int updatedMoveCount = game.moveCount() + 1;

        Game updatedGame = Game.builder()
                .id(game.id())
                .status(resolveStatus(isWinner, player, updatedMoveCount))
                .currentPlayer(isWinner || updatedMoveCount == 9 ? game.currentPlayer() : game.nextPlayer())
                .player1Symbol(game.player1Symbol())
                .winner(isWinner ? player : null)
                .moveCount(updatedMoveCount)
                .version(game.version())
                .build();
        return gameRepositoryPort.save(updatedGame);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Move> getMoves(UUID gameId) {
        return moveRepositoryPort.findAllByGameIdOrderByMoveNumberAsc(gameId);
    }

    private void validateMove(Game game, Player player, int position) {
        if (game.isFinished()) {
            throw new GameAlreadyFinishedException();
        }

        if (game.currentPlayer() != player) {
            throw new InvalidTurnException(player);
        }

        if (moveRepositoryPort.existsByGameIdAndPosition(game.id(), position)) {
            throw new PositionAlreadyTakenException(position);
        }
    }

    private boolean evalWinner(List<Move> moves, Player player) {
        Set<Integer> positions = moves.stream()
                .filter(move -> move.player() == player)
                .map(Move::position)
                .collect(Collectors.toSet());

        return WINNING_COMBINATIONS.stream().anyMatch(positions::containsAll);
    }

    private GameStatus resolveStatus(boolean winner, Player player, int moveCount) {
        if (winner) {
            return player == Player.PLAYER_1
                    ? GameStatus.PLAYER1_WON
                    : GameStatus.PLAYER2_WON;
        }

        if (moveCount == 9) {
            return GameStatus.DRAW;
        }

        return GameStatus.IN_PROGRESS;
    }
}
