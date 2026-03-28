package com.sercan.tictactoe_basic.adapter.in.web.dto.request;

import com.sercan.tictactoe_basic.domain.model.Symbol;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequestDto(
        @NotNull Symbol playerOneSymbol
) {
}
