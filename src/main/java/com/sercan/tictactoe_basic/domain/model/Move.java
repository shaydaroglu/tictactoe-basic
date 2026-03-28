package com.sercan.tictactoe_basic.domain.model;

import lombok.Builder;

@Builder
public record Move(int position, Player player, int moveNumber) {
}
