package com.agutsul.chess;

import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;

import java.util.EnumSet;
import java.util.Set;

import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface Promotable {

    Set<Piece.Type> TARGET_TYPES = EnumSet.of(BISHOP, KNIGHT, ROOK, QUEEN);

    default void promote(Position position, Piece.Type pieceType) {}
}