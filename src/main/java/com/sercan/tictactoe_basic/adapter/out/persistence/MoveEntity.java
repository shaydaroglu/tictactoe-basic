package com.sercan.tictactoe_basic.adapter.out.persistence;

import com.sercan.tictactoe_basic.domain.model.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "moves",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_game_position", columnNames = {"game_id", "position"}),
                @UniqueConstraint(name = "uk_game_move_number", columnNames = {"game_id", "move_number"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Player player;

    @Column(nullable = false)
    private int moveNumber;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
