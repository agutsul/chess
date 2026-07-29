package com.agutsul.chess;

import java.util.Collection;

import com.agutsul.chess.piece.Piece;

public interface Protectable {

    boolean isProtected();

    Collection<Piece<?>> getProtectors();

    Collection<Piece<?>> getProtected();
}