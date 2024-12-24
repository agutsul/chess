package com.agutsul.chess.piece.cache;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface PieceCache {

    void refresh();

    Collection<Piece<?>> getAll();

    Collection<Piece<?>> get(Color color);
    Collection<Piece<?>> get(Piece.Type pieceType);
    Collection<Piece<?>> get(Color color, Piece.Type pieceType);

    Optional<Piece<?>> get(Position position);
}