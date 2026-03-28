package com.sercan.tictactoe_basic.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MoveJpaRepository extends JpaRepository<MoveEntity, Long> {
    List<MoveEntity> findAllByGameIdOrderByMoveNumberAsc(UUID gameId);

    boolean existsByGameIdAndPosition(UUID gameId, int position);
}
