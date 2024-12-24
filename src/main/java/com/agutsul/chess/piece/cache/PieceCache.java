package com.agutsul.chess.piece.cache;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface PieceCache {

    void refresh();

    Collection<Piece<?>> getActive();
    Collection<Piece<?>> getActive(Color color);
    Collection<Piece<?>> getActive(Piece.Type pieceType);
    Collection<Piece<?>> getActive(Color color, Piece.Type pieceType);

    Optional<Piece<?>> getActive(Position position);

    Collection<Piece<?>> getCaptured(Color color, Position position);
}