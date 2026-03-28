package com.sercan.tictactoe_basic.adapter.in.web.dto.response;

import java.time.Instant;

public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message
) {
}
