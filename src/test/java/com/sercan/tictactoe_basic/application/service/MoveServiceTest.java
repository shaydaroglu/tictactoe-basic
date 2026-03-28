package com.sercan.tictactoe_basic.application.service;

import com.sercan.tictactoe_basic.application.exception.GameAlreadyFinishedException;
import com.sercan.tictactoe_basic.application.exception.GameNotFoundException;
import com.sercan.tictactoe_basic.application.exception.InvalidTurnException;
import com.sercan.tictactoe_basic.application.exception.PositionAlreadyTakenException;
import com.sercan.tictactoe_basic.application.port.out.GameRepositoryPort;
import com.sercan.tictactoe_basic.application.port.out.MoveRepositoryPort;
import com.sercan.tictactoe_basic.common.TestLoggerExtension;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Move;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Move Service Unit Tests")
@ExtendWith(TestLoggerExtension.class)
class MoveServiceTest {

    private final InMemoryGameRepository gameRepository = new InMemoryGameRepository();
    private final InMemoryMoveRepository moveRepository = new InMemoryMoveRepository();
    private final MoveService moveService = new MoveService(gameRepository, moveRepository);

    @Test
    @DisplayName("stores a move and switches the turn to the other player")
    void makeMoveStoresMoveAndSwitchesTurn() {
        Game game = saveNewGame();

        Game updated = moveService.makeMove(game.id(), Player.PLAYER_1, 0);

        assertEquals(GameStatus.IN_PROGRESS, updated.status());
        assertEquals(Player.PLAYER_2, updated.currentPlayer());
        assertNull(updated.winner());
        assertEquals(1, updated.moveCount());
        assertEquals(List.of(0), positionsForGame(game.id()));
    }

    @Test
    @DisplayName("marks player one as winner after a winning row is completed")
    void makeMoveMarksPlayerOneAsWinner() {
        Game game = saveNewGame();

        moveService.makeMove(game.id(), Player.PLAYER_1, 0);
        moveService.makeMove(game.id(), Player.PLAYER_2, 3);
        moveService.makeMove(game.id(), Player.PLAYER_1, 1);
        moveService.makeMove(game.id(), Player.PLAYER_2, 4);

        Game updated = moveService.makeMove(game.id(), Player.PLAYER_1, 2);

        assertEquals(GameStatus.PLAYER1_WON, updated.status());
        assertEquals(Player.PLAYER_1, updated.winner());
        assertEquals(Player.PLAYER_1, updated.currentPlayer());
        assertEquals(5, updated.moveCount());
    }

    @Test
    @DisplayName("marks player two as winner after a winning row is completed")
    void makeMoveMarksPlayerTwoAsWinner() {
        Game game = saveNewGame();

        moveService.makeMove(game.id(), Player.PLAYER_1, 3);
        moveService.makeMove(game.id(), Player.PLAYER_2, 0);
        moveService.makeMove(game.id(), Player.PLAYER_1, 4);
        moveService.makeMove(game.id(), Player.PLAYER_2, 1);
        moveService.makeMove(game.id(), Player.PLAYER_1, 8);

        Game updated = moveService.makeMove(game.id(), Player.PLAYER_2, 2);

        assertEquals(GameStatus.PLAYER2_WON, updated.status());
        assertEquals(Player.PLAYER_2, updated.winner());
        assertEquals(Player.PLAYER_2, updated.currentPlayer());
        assertEquals(6, updated.moveCount());
    }

    @Test
    @DisplayName("marks the game as draw on the ninth non-winning move")
    void makeMoveMarksGameAsDrawOnNinthMove() {
        Game game = saveNewGame();

        moveService.makeMove(game.id(), Player.PLAYER_1, 0);
        moveService.makeMove(game.id(), Player.PLAYER_2, 1);
        moveService.makeMove(game.id(), Player.PLAYER_1, 2);
        moveService.makeMove(game.id(), Player.PLAYER_2, 4);
        moveService.makeMove(game.id(), Player.PLAYER_1, 3);
        moveService.makeMove(game.id(), Player.PLAYER_2, 5);
        moveService.makeMove(game.id(), Player.PLAYER_1, 7);
        moveService.makeMove(game.id(), Player.PLAYER_2, 6);

        Game updated = moveService.makeMove(game.id(), Player.PLAYER_1, 8);

        assertEquals(GameStatus.DRAW, updated.status());
        assertNull(updated.winner());
        assertEquals(Player.PLAYER_1, updated.currentPlayer());
        assertEquals(9, updated.moveCount());
    }

    @Test
    @DisplayName("throws when trying to move in a game that does not exist")
    void makeMoveThrowsWhenGameIsMissing() {
        assertThrows(
                GameNotFoundException.class,
                () -> moveService.makeMove(UUID.randomUUID(), Player.PLAYER_1, 0)
        );
    }

    @Test
    @DisplayName("throws when a player moves out of turn")
    void makeMoveThrowsWhenWrongPlayerTriesToMove() {
        Game game = saveNewGame();

        assertThrows(
                InvalidTurnException.class,
                () -> moveService.makeMove(game.id(), Player.PLAYER_2, 0)
        );
    }

    @Test
    @DisplayName("throws when the chosen position is already taken")
    void makeMoveThrowsWhenPositionIsAlreadyTaken() {
        Game game = saveNewGame();
        moveService.makeMove(game.id(), Player.PLAYER_1, 0);

        assertThrows(
                PositionAlreadyTakenException.class,
                () -> moveService.makeMove(game.id(), Player.PLAYER_2, 0)
        );
    }

    @Test
    @DisplayName("throws when trying to move after the game has finished")
    void makeMoveThrowsWhenGameIsAlreadyFinished() {
        Game finishedGame = Game.builder()
                .id(UUID.randomUUID())
                .status(GameStatus.PLAYER1_WON)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(Symbol.X)
                .winner(Player.PLAYER_1)
                .moveCount(5)
                .version(0L)
                .build();
        gameRepository.save(finishedGame);

        assertThrows(
                GameAlreadyFinishedException.class,
                () -> moveService.makeMove(finishedGame.id(), Player.PLAYER_1, 6)
        );
    }

    @Test
    @DisplayName("returns moves ordered by ascending move number")
    void getMovesReturnsMovesInAscendingOrder() {
        Game game = saveNewGame();

        moveService.makeMove(game.id(), Player.PLAYER_1, 4);
        moveService.makeMove(game.id(), Player.PLAYER_2, 0);
        moveService.makeMove(game.id(), Player.PLAYER_1, 8);

        List<Move> moves = moveService.getMoves(game.id());

        assertEquals(3, moves.size());
        assertIterableEquals(List.of(1, 2, 3), moves.stream().map(Move::moveNumber).toList());
        assertIterableEquals(List.of(4, 0, 8), moves.stream().map(Move::position).toList());
    }

    @Test
    @DisplayName("returns an empty move list for a new game")
    void getMovesReturnsEmptyListForNewGame() {
        Game game = saveNewGame();

        List<Move> moves = moveService.getMoves(game.id());

        assertEquals(List.of(), moves);
    }

    @Test
    @DisplayName("keeps alternating the current player after non-winning moves")
    void makeMoveContinuesAlternatingPlayersAfterNonWinningMoves() {
        Game game = saveNewGame();

        Game afterFirstMove = moveService.makeMove(game.id(), Player.PLAYER_1, 0);
        Game afterSecondMove = moveService.makeMove(game.id(), Player.PLAYER_2, 4);
        Game afterThirdMove = moveService.makeMove(game.id(), Player.PLAYER_1, 8);

        assertEquals(Player.PLAYER_2, afterFirstMove.currentPlayer());
        assertEquals(Player.PLAYER_1, afterSecondMove.currentPlayer());
        assertEquals(Player.PLAYER_2, afterThirdMove.currentPlayer());
        assertEquals(GameStatus.IN_PROGRESS, afterThirdMove.status());
        assertEquals(3, afterThirdMove.moveCount());
    }

    private Game saveNewGame() {
        return gameRepository.save(Game.builder()
                .status(GameStatus.IN_PROGRESS)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(Symbol.X)
                .moveCount(0)
                .build());
    }

    private List<Integer> positionsForGame(UUID gameId) {
        return moveRepository.findAllByGameIdOrderByMoveNumberAsc(gameId).stream()
                .map(Move::position)
                .toList();
    }

    private static final class InMemoryGameRepository implements GameRepositoryPort {
        private final Map<UUID, Game> games = new HashMap<>();

        @Override
        public Game save(Game game) {
            UUID id = game.id() != null ? game.id() : UUID.randomUUID();
            Long version = game.version() == null ? 0L : game.version() + 1;

            Game saved = Game.builder()
                    .id(id)
                    .status(game.status())
                    .currentPlayer(game.currentPlayer())
                    .player1Symbol(game.player1Symbol())
                    .winner(game.winner())
                    .moveCount(game.moveCount())
                    .version(version)
                    .build();

            games.put(id, saved);
            return saved;
        }

        @Override
        public Optional<Game> findById(UUID id) {
            return Optional.ofNullable(games.get(id));
        }
    }

    private static final class InMemoryMoveRepository implements MoveRepositoryPort {
        private final Map<UUID, List<Move>> movesByGame = new HashMap<>();
        private long nextId = 1L;

        @Override
        public Move save(Move move) {
            Move saved = Move.builder()
                    .id(nextId++)
                    .gameId(move.gameId())
                    .position(move.position())
                    .player(move.player())
                    .moveNumber(move.moveNumber())
                    .build();

            movesByGame.computeIfAbsent(move.gameId(), ignored -> new ArrayList<>()).add(saved);
            return saved;
        }

        @Override
        public List<Move> findAllByGameIdOrderByMoveNumberAsc(UUID gameId) {
            return movesByGame.getOrDefault(gameId, List.of()).stream()
                    .sorted(Comparator.comparingInt(Move::moveNumber))
                    .toList();
        }

        @Override
        public boolean existsByGameIdAndPosition(UUID gameId, int position) {
            return movesByGame.getOrDefault(gameId, List.of()).stream()
                    .anyMatch(move -> move.position() == position);
        }
    }
}
