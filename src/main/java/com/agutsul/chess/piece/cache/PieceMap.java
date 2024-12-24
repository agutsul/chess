package com.agutsul.chess.piece.cache;

import java.util.Collection;
import java.util.Map;

import com.agutsul.chess.piece.Piece;

public interface PieceMap
    extends Map<String, Collection<Piece<?>>> {

}
