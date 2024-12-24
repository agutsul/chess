package com.agutsul.chess.piece.cache;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.position.Position;

public class PieceCacheImpl implements PieceCache {

    private final Set<Piece<?>> pieces;
    private final PieceMap pieceMap;

    public PieceCacheImpl(Collection<Piece<?>> pieces) {
        this.pieces = new HashSet<Piece<?>>(pieces);
        this.pieceMap = new PieceMultiMap();

        refresh();
    }

    @Override
    public void refresh() {
        var map = new PieceMultiMap();

        var activePieces = this.pieces.stream()
                .filter(Piece::isActive)
                .toList();

        for (var color : Colors.values()) {
            var piecesByColor = activePieces.stream()
                    .filter(piece -> Objects.equals(piece.getColor(), color))
                    .toList();

            for (var pieceType : Piece.Type.values()) {
                var piecesByType = piecesByColor.stream()
                        .filter(piece -> Objects.equals(piece.getType(), pieceType))
                        .toList();

                // pieces by piece type
                map.put(createKey(pieceType), piecesByType);

                // pieces by color and piece type
                map.put(createKey(color, pieceType), piecesByType);
            }

            // pieces by color
            map.put(createKey(color), piecesByColor);
        }

        for (var piece : activePieces) {
            // pieces by position
            map.put(createKey(piece.getPosition()), List.of(piece));
        }

        this.pieceMap.clear();
        this.pieceMap.putAll(map);
    }

    @Override
    public Collection<Piece<?>> getAll() {
        return unmodifiableSet(this.pieces);
    }

    @Override
    public Collection<Piece<?>> get(Color color) {
        return get(createKey(color));
    }

    @Override
    public Collection<Piece<?>> get(Type pieceType) {
        return get(createKey(pieceType));
    }

    @Override
    public Collection<Piece<?>> get(Color color, Type pieceType) {
        return get(createKey(color, pieceType));
    }

    @Override
    public Optional<Piece<?>> get(Position position) {
        var pieces = get(createKey(position));
        if (pieces.isEmpty()) {
            return Optional.empty();
        }

        var piece = pieces.iterator().next();
        return Optional.of(piece);
    }

    private Collection<Piece<?>> get(String key) {
        var pieces = this.pieceMap.getOrDefault(key, emptyList());
        return unmodifiableCollection(pieces);
    }

    // utility methods

    private static String createKey(Position position) {
        return createKey(String.valueOf(position));
    }

    private static String createKey(Piece.Type pieceType) {
        return createKey(String.valueOf(pieceType));
    }

    private static String createKey(Color color) {
        return createKey(String.valueOf(color));
    }

    private static String createKey(Color color, Piece.Type pieceType) {
        return createKey(String.valueOf(color), String.valueOf(pieceType));
    }

    private static String createKey(String string, String... strings) {
        var list = new ArrayList<String>();

        list.add(string);
        list.addAll(List.of(strings));

        return join(list, "_");
    }
}