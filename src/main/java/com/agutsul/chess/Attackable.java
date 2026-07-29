package com.agutsul.chess;

import java.util.Collection;

import com.agutsul.chess.piece.Piece;

public interface Attackable {

    boolean isAttacked();

    Collection<Piece<?>> getAttackers();

    Collection<Piece<?>> getAttacked();
}