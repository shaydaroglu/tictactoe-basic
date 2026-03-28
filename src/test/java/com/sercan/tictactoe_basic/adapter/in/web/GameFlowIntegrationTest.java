package com.sercan.tictactoe_basic.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import com.sercan.tictactoe_basic.common.TestLoggerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(TestLoggerExtension.class)
@DisplayName("Game Flow Integration Tests")
class GameFlowIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("creates a game, persists moves, and returns the final winning board state")
    void createMoveAndFetchFlowPersistsBoardState() throws Exception {
        MvcResult createResponse = mockMvc.perform(post("/api/v1/game")
                        .contentType("application/json")
                        .content("""
                                {"playerOneSymbol":"X"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String gameId = JsonPath.read(createResponse.getResponse().getContentAsString(), "$.gameId");

        assertEquals(200, performMove(gameId, "PLAYER_1", 0).getResponse().getStatus());
        assertEquals(200, performMove(gameId, "PLAYER_2", 3).getResponse().getStatus());
        assertEquals(200, performMove(gameId, "PLAYER_1", 1).getResponse().getStatus());
        assertEquals(200, performMove(gameId, "PLAYER_2", 4).getResponse().getStatus());

        MvcResult winningMoveResponse = performMove(gameId, "PLAYER_1", 2);
        String winningMoveBody = winningMoveResponse.getResponse().getContentAsString();
        assertEquals(200, winningMoveResponse.getResponse().getStatus());
        assertEquals("PLAYER1_WON", JsonPath.read(winningMoveBody, "$.status"));
        assertEquals("PLAYER_1", JsonPath.read(winningMoveBody, "$.winner"));

        MvcResult getGameResponse = mockMvc.perform(get("/api/v1/game/{id}", gameId))
                .andExpect(status().isOk())
                .andReturn();
        String gameBody = getGameResponse.getResponse().getContentAsString();
        assertEquals(gameId, JsonPath.read(gameBody, "$.gameId"));
        assertEquals("PLAYER1_WON", JsonPath.read(gameBody, "$.status"));
        assertEquals("PLAYER_1", JsonPath.read(gameBody, "$.winner"));
        assertEquals(5, ((Number) JsonPath.read(gameBody, "$.moveCount")).intValue());
        assertEquals("X", JsonPath.read(gameBody, "$.board[0][0]"));
        assertEquals("X", JsonPath.read(gameBody, "$.board[0][1]"));
        assertEquals("X", JsonPath.read(gameBody, "$.board[0][2]"));
        assertEquals("O", JsonPath.read(gameBody, "$.board[1][0]"));
        assertEquals("O", JsonPath.read(gameBody, "$.board[1][1]"));
    }

    private MvcResult performMove(String gameId, String player, int position) throws Exception {
        return mockMvc.perform(post("/api/v1/game/{id}/moves", gameId)
                        .contentType("application/json")
                        .content("""
                                {"player":"%s","position":%s}
                                """.formatted(player, position)))
                .andExpect(status().isOk())
                .andReturn();
    }
}
