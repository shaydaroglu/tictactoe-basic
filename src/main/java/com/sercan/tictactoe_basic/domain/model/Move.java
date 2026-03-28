package com.sercan.tictactoe_basic.domain.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Move(Long id, UUID gameId, int position, Player player, int moveNumber) {
}
