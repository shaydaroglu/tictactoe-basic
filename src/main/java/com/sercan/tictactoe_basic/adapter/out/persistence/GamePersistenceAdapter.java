package com.sercan.tictactoe_basic.adapter.out.persistence;

import com.sercan.tictactoe_basic.application.port.out.GameRepositoryPort;
import com.sercan.tictactoe_basic.application.port.out.MoveRepositoryPort;
import com.sercan.tictactoe_basic.domain.model.Game;
import com.sercan.tictactoe_basic.domain.model.Move;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GamePersistenceAdapter implements GameRepositoryPort, MoveRepositoryPort {
    private final GameJpaRepository gameJpaRepository;
    private final MoveJpaRepository moveJpaRepository;


    @Override
    public Game save(Game game) {
        GameEntity entity = mapToGameEntity(game);
        GameEntity savedEntity = gameJpaRepository.save(entity);
        return mapToGame(savedEntity);
    }

    @Override
    public Optional<Game> findById(UUID id) {
        return gameJpaRepository.findById(id)
                .map(this::mapToGame);
    }

    @Override
    public Move save(Move move) {
        GameEntity gameEntity = gameJpaRepository.findById(move.gameId())
                .orElseThrow(() -> new IllegalArgumentException("Game with id " + move.gameId() + " does not exist"));

        MoveEntity entity = mapToMoveEntity(move, gameEntity);
        MoveEntity savedEntity = moveJpaRepository.save(entity);
        return mapToMove(savedEntity);
    }

    @Override
    public List<Move> findAllByGameIdOrderByMoveNumberAsc(UUID gameId) {
        return moveJpaRepository.findAllByGameIdOrderByMoveNumberAsc(gameId).stream()
                .map(this::mapToMove)
                .toList();
    }

    @Override
    public boolean existsByGameIdAndPosition(UUID gameId, int position) {
        return moveJpaRepository.existsByGameIdAndPosition(gameId, position);
    }

    private Game mapToGame(GameEntity entity) {
        return Game.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .currentPlayer(entity.getCurrentPlayer())
                .player1Symbol(entity.getPlayer1Symbol())
                .winner(entity.getWinner())
                .moveCount(entity.getMoveCount())
                .version(entity.getVersion())
                .build();
    }

    private GameEntity mapToGameEntity(Game game) {
        return GameEntity.builder()
                .id(game.id())
                .status(game.status())
                .currentPlayer(game.currentPlayer())
                .player1Symbol(game.player1Symbol())
                .winner(game.winner())
                .moveCount(game.moveCount())
                .version(game.version())
                .build();
    }

    private Move mapToMove(MoveEntity entity) {
        return Move.builder()
                .id(entity.getId())
                .gameId(entity.getGame().getId())
                .position(entity.getPosition())
                .player(entity.getPlayer())
                .moveNumber(entity.getMoveNumber())
                .build();
    }

    private MoveEntity mapToMoveEntity(Move move, GameEntity gameEntity) {
        return MoveEntity.builder()
                .id(move.id())
                .game(gameEntity)
                .position(move.position())
                .player(move.player())
                .moveNumber(move.moveNumber())
                .build();
    }
}
