package com.agutsul.chess.piece.rook;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.AbstractSkewerLineAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class RookSkewerAlgo<COLOR extends Color,
                           ROOK extends RookPiece<COLOR>>
        extends AbstractSkewerLineAlgo<COLOR,ROOK> {

    RookSkewerAlgo(Board board, Algo<ROOK,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    protected Algo<ROOK,Collection<Line>> createPieceAlgo(Board board) {
        return new RookPieceAlgo<>(board);
    }
}