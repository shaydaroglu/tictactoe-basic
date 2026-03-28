package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.application.exception.GameNotFoundException;
import com.sercan.tictactoe_basic.application.exception.InvalidTurnException;
import com.sercan.tictactoe_basic.application.port.in.GameUseCase;
import com.sercan.tictactoe_basic.application.port.in.MoveUseCase;
import com.sercan.tictactoe_basic.common.TestLoggerExtension;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.GameStatus;
import com.sercan.tictactoe_basic.domain.model.Move;
import com.sercan.tictactoe_basic.domain.model.Player;
import com.sercan.tictactoe_basic.domain.model.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Game Controller Tests")
@ExtendWith({MockitoExtension.class, TestLoggerExtension.class})
class GameControllerTest {

    @Mock
    private GameUseCase gameUseCase;

    @Mock
    private MoveUseCase moveUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        GameController gameController = new GameController(gameUseCase, moveUseCase, new GameResponseMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(gameController)
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/game returns a new game response with example board values")
    void createGameReturnsNewGameBoard() throws Exception {
        UUID gameId = UUID.randomUUID();
        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.IN_PROGRESS)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(Symbol.X)
                .moveCount(0)
                .version(0L)
                .build();

        when(gameUseCase.createGame(Symbol.X)).thenReturn(game);

        mockMvc.perform(post("/api/v1/game")
                        .contentType("application/json")
                        .content("""
                                {"playerOneSymbol":"X"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId.toString()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.currentPlayer").value("PLAYER_1"))
                .andExpect(jsonPath("$.playerOneSymbol").value("X"))
                .andExpect(jsonPath("$.moveCount").value(0))
                .andExpect(jsonPath("$.board[0][0]").value("0"))
                .andExpect(jsonPath("$.board[2][2]").value("8"));
    }

    @Test
    @DisplayName("POST /api/v1/game returns player one as O when requested")
    void createGameReturnsRequestedSymbolO() throws Exception {
        UUID gameId = UUID.randomUUID();
        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.IN_PROGRESS)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(Symbol.O)
                .moveCount(0)
                .version(0L)
                .build();

        when(gameUseCase.createGame(Symbol.O)).thenReturn(game);

        mockMvc.perform(post("/api/v1/game")
                        .contentType("application/json")
                        .content("""
                                {"playerOneSymbol":"O"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId.toString()))
                .andExpect(jsonPath("$.playerOneSymbol").value("O"))
                .andExpect(jsonPath("$.currentPlayer").value("PLAYER_1"))
                .andExpect(jsonPath("$.board[0][0]").value("0"))
                .andExpect(jsonPath("$.board[2][2]").value("8"));
    }

    @Test
    @DisplayName("GET /api/v1/game/{id} returns the mapped board state with played moves")
    void getGameReturnsMappedBoardWithMoves() throws Exception {
        UUID gameId = UUID.randomUUID();
        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.IN_PROGRESS)
                .currentPlayer(Player.PLAYER_1)
                .player1Symbol(Symbol.X)
                .moveCount(2)
                .version(1L)
                .build();
        List<Move> moves = List.of(
                Move.builder().id(1L).gameId(gameId).position(0).player(Player.PLAYER_1).moveNumber(1).build(),
                Move.builder().id(2L).gameId(gameId).position(4).player(Player.PLAYER_2).moveNumber(2).build()
        );

        when(gameUseCase.getGame(gameId)).thenReturn(game);
        when(moveUseCase.getMoves(gameId)).thenReturn(moves);

        mockMvc.perform(get("/api/v1/game/{id}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.board[0][0]").value("X"))
                .andExpect(jsonPath("$.board[1][1]").value("O"))
                .andExpect(jsonPath("$.board[0][1]").value(""));
    }

    @Test
    @DisplayName("POST /api/v1/game/{id}/moves returns 404 when the game does not exist")
    void makeMoveReturnsNotFoundWhenGameDoesNotExist() throws Exception {
        UUID gameId = UUID.randomUUID();
        when(moveUseCase.makeMove(eq(gameId), eq(Player.PLAYER_1), eq(0)))
                .thenThrow(new GameNotFoundException(gameId));

        mockMvc.perform(post("/api/v1/game/{id}/moves", gameId)
                        .contentType("application/json")
                        .content("""
                                {"player":"PLAYER_1","position":0}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Game not found with id: '" + gameId + "'"));
    }

    @Test
    @DisplayName("POST /api/v1/game/{id}/moves returns 400 for an invalid turn")
    void makeMoveReturnsBadRequestForInvalidTurn() throws Exception {
        UUID gameId = UUID.randomUUID();
        when(moveUseCase.makeMove(eq(gameId), eq(Player.PLAYER_2), eq(0)))
                .thenThrow(new InvalidTurnException(Player.PLAYER_2));

        mockMvc.perform(post("/api/v1/game/{id}/moves", gameId)
                        .contentType("application/json")
                        .content("""
                                {"player":"PLAYER_2","position":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("It is not PLAYER_2's turn"));
    }

    @Test
    @DisplayName("POST /api/v1/game returns 400 for an unsupported player symbol")
    void createGameReturnsBadRequestForInvalidSymbol() throws Exception {
        mockMvc.perform(post("/api/v1/game")
                        .contentType("application/json")
                        .content("""
                                {"playerOneSymbol":"Z"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("playerOneSymbol must be one of: X, O"));
    }

    @Test
    @DisplayName("POST /api/v1/game/{id}/moves returns 400 when player is missing")
    void makeMoveReturnsBadRequestForValidationFailure() throws Exception {
        mockMvc.perform(post("/api/v1/game/{id}/moves", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"position":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("player must not be null"));
    }

    @Test
    @DisplayName("POST /api/v1/game/{id}/moves returns 400 when position is out of range")
    void makeMoveReturnsBadRequestForOutOfRangePosition() throws Exception {
        mockMvc.perform(post("/api/v1/game/{id}/moves", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"player":"PLAYER_1","position":9}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("position must be less than or equal to 8"));
    }
}
