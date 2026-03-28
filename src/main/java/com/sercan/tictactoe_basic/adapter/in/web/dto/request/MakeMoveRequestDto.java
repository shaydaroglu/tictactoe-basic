package com.sercan.tictactoe_basic.adapter.in.web.dto.request;

import com.sercan.tictactoe_basic.domain.model.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MakeMoveRequestDto(
        @Schema(
                description = "Player making the move",
                allowableValues = {"PLAYER_1", "PLAYER_2"},
                example = "PLAYER_1"
        )
        @NotNull Player player,
        @Schema(
                description = "Board position from 0 to 8, read left-to-right and top-to-bottom",
                minimum = "0",
                maximum = "8",
                example = "4"
        )
        @Min(0) @Max(8) int position
        ) {
}
