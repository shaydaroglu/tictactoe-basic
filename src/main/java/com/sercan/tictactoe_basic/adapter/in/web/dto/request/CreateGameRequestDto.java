package com.sercan.tictactoe_basic.adapter.in.web.dto.request;

import com.sercan.tictactoe_basic.domain.model.Symbol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequestDto(
        @Schema(
                description = "Symbol assigned to player one",
                allowableValues = {"X", "O"},
                example = "X"
        )
        @NotNull Symbol playerOneSymbol
) {
}
