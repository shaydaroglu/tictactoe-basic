package com.sercan.tictactoe_basic.adapter.in.web.dto.request;

import com.sercan.tictactoe_basic.domain.model.Player;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MakeMoveRequestDto(
        @NotNull Player player,
        @Min(0) @Max(8) int position
        ) {
}
