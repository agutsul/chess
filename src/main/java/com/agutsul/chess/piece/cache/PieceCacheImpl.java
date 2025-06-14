package com.agutsul.chess.piece.cache;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.position.Position;

public class PieceCacheImpl implements PieceCache {

    private static final Logger LOGGER = getLogger(PieceCacheImpl.class);

    private static final String ACTIVE_KEY = StringUtils.EMPTY;

    private final Set<Piece<?>> pieces;
    private final KeyFactory keyFactory;
    private final PieceMap pieceMap;
    private final ExecutorService executorService;

    public PieceCacheImpl(Collection<Piece<?>> pieces,
                          ExecutorService executorService) {

        this.pieces = new HashSet<Piece<?>>(pieces);
        this.executorService = executorService;

        this.keyFactory = new KeyFactoryImpl();
        this.pieceMap = new PieceMultiMap();
    }

    @Override
    public void refresh() {
        var tasks = List.of(
                new ActivePieceTask(pieces,   keyFactory, piece ->  piece.isActive()),
                new CapturedPieceTask(pieces, keyFactory, piece -> !piece.isActive())
        );

        try {
            var map = new PieceMultiMap();
            for (var future : this.executorService.invokeAll(tasks)) {
                map.putAll(future.get());
            }

            this.pieceMap.clear();
            this.pieceMap.putAll(map);

        } catch (InterruptedException e) {
            throw new GameInterruptionException("Refreshing board cache interrupted");
        } catch (ExecutionException e) {
            LOGGER.error("Refreshing board cache failed", e);
        }
    }

    @Override
    public Collection<Piece<?>> getActive() {
        return get(ACTIVE_KEY);
    }

    @Override
    public Collection<Piece<?>> getActive(Color color) {
        return get(keyFactory.createKey(color));
    }

    @Override
    public Collection<Piece<?>> getActive(Type pieceType) {
        return get(keyFactory.createKey(pieceType));
    }

    @Override
    public Collection<Piece<?>> getActive(Color color, Type pieceType) {
        return get(keyFactory.createKey(color, pieceType));
    }

    @Override
    public Optional<Piece<?>> getActive(Position position) {
        var pieces = get(keyFactory.createKey(position));
        if (pieces.isEmpty()) {
            return Optional.empty();
        }

        var piece = pieces.iterator().next();
        return Optional.of(piece);
    }

    @Override
    public Collection<Piece<?>> getCaptured(Color color, Position position) {
        return get(keyFactory.createKey(color, position));
    }

    private Collection<Piece<?>> get(String key) {
        var pieces = this.pieceMap.getOrDefault(key, emptyList());
        return unmodifiableCollection(pieces);
    }

    interface KeyFactory {
        String createKey(Position position);
        String createKey(Piece.Type pieceType);
        String createKey(Color color);
        String createKey(Color color, Position position);
        String createKey(Color color, Piece.Type pieceType);
    }

    private static abstract class AbstractPieceTask
            implements Callable<PieceMap> {

        private final Collection<Piece<?>> pieces;
        private final Predicate<Piece<?>> predicate;

        final KeyFactory keyFactory;

        AbstractPieceTask(Collection<Piece<?>> pieces,
                          KeyFactory keyFactory,
                          Predicate<Piece<?>> predicate) {

            this.pieces = pieces;
            this.keyFactory = keyFactory;
            this.predicate = predicate;
        }

        abstract PieceMap createPieceMap(Collection<Piece<?>> pieces);

        @Override
        public PieceMap call() throws Exception {
            var filteredPieces = this.pieces.stream()
                    .filter(piece -> predicate.test(piece))
                    .toList();

            return createPieceMap(filteredPieces);
        }
    }

    private static final class ActivePieceTask
            extends AbstractPieceTask {

        ActivePieceTask(Collection<Piece<?>> pieces,
                        KeyFactory keyFactory,
                        Predicate<Piece<?>> predicate) {

            super(pieces, keyFactory, predicate);
        }

        @Override
        PieceMap createPieceMap(Collection<Piece<?>> pieces) {
            var map = new PieceMultiMap();

            for (var color : Colors.values()) {
                var piecesByColor = pieces.stream()
                        .filter(piece -> Objects.equals(piece.getColor(), color))
                        .toList();

                for (var pieceType : Piece.Type.values()) {
                    var piecesByType = piecesByColor.stream()
                            .filter(piece -> Objects.equals(piece.getType(), pieceType))
                            .toList();

                    // pieces by piece type
                    map.put(keyFactory.createKey(pieceType), piecesByType);

                    // pieces by color and piece type
                    map.put(keyFactory.createKey(color, pieceType), piecesByType);
                }

                // pieces by color
                map.put(keyFactory.createKey(color), piecesByColor);
            }

            for (var piece : pieces) {
                // pieces by position
                map.put(keyFactory.createKey(piece.getPosition()), List.of(piece));
            }

            // active pieces
            map.put(ACTIVE_KEY, pieces);
            return map;
        }
    }

    private static final class CapturedPieceTask
            extends AbstractPieceTask {

        CapturedPieceTask(Collection<Piece<?>> pieces,
                          KeyFactory keyFactory,
                          Predicate<Piece<?>> predicate) {

            super(pieces, keyFactory, predicate);
        }

        @Override
        PieceMap createPieceMap(Collection<Piece<?>> pieces) {
            var map = new PieceMultiMap();

            // captured piece
            for (var piece : pieces) {
                var key = keyFactory.createKey(piece.getColor(), piece.getPosition());
                map.put(key, List.of(piece));
            }

            return map;
        }
    }

    private static final class KeyFactoryImpl
            implements KeyFactory {

        @Override
        public String createKey(Position position) {
            return String.valueOf(position);
        }

        @Override
        public String createKey(Type pieceType) {
            return String.valueOf(pieceType.name());
        }

        @Override
        public String createKey(Color color) {
            return String.valueOf(color);
        }

        @Override
        public String createKey(Color color, Position position) {
            return createKey(createKey(color), createKey(position));
        }

        @Override
        public String createKey(Color color, Type pieceType) {
            return createKey(createKey(color), createKey(pieceType));
        }

        private static String createKey(String string, String... strings) {
            return Stream.of(List.of(string), List.of(strings))
                    .flatMap(Collection::stream)
                    .collect(joining("_"));
        }
    }
}