package com.sercan.tictactoe_basic.adapter.in.web.dto.request;

import com.sercan.tictactoe_basic.domain.model.Symbol;

public record CreateGameRequestDto(
        Symbol playerOneSymbol
) {
}
