package com.sercan.tictactoe_basic.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard error response returned by the API")
public record ErrorResponseDto(
        @Schema(description = "Time at which the error was generated", example = "2026-03-28T18:00:00Z")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP reason phrase", example = "Bad Request")
        String error,
        @Schema(description = "Human-readable error message", example = "playerOneSymbol must be one of: X, O")
        String message
) {
}
