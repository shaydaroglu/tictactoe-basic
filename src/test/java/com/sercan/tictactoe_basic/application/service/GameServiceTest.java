package com.sercan.tictactoe_basic.application.service;

import com.sercan.tictactoe_basic.application.exception.GameNotFoundException;
import com.sercan.tictactoe_basic.application.port.out.GameRepositoryPort;
import com.sercan.tictactoe_basic.common.TestLoggerExtension;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Game Services Unit Tests")
@ExtendWith(TestLoggerExtension.class)
class GameServiceTest {

    private final InMemoryGameRepository gameRepository = new InMemoryGameRepository();
    private final GameService gameService = new GameService(gameRepository);

    @Test
    @DisplayName("creates a new game with the expected initial state")
    void createGameInitializesExpectedState() {
        Game game = gameService.createGame(Symbol.X);

        assertNotNull(game.id());
        assertEquals(GameStatus.IN_PROGRESS, game.status());
        assertEquals(Player.PLAYER_1, game.currentPlayer());
        assertEquals(Symbol.X, game.player1Symbol());
        assertEquals(0, game.moveCount());
    }

    @Test
    @DisplayName("creates a new game with player one as O when requested")
    void createGameSupportsPlayerOneAsO() {
        Game game = gameService.createGame(Symbol.O);

        assertEquals(Symbol.O, game.player1Symbol());
        assertEquals(Player.PLAYER_1, game.currentPlayer());
        assertEquals(GameStatus.IN_PROGRESS, game.status());
    }

    @Test
    @DisplayName("returns a persisted game by id")
    void getGameReturnsPersistedGame() {
        Game created = gameService.createGame(Symbol.O);

        Game loaded = gameService.getGame(created.id());

        assertEquals(created, loaded);
    }

    @Test
    @DisplayName("throws when the requested game does not exist")
    void getGameThrowsWhenGameIsMissing() {
        UUID unknownId = UUID.randomUUID();

        assertThrows(GameNotFoundException.class, () -> gameService.getGame(unknownId));
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
}
