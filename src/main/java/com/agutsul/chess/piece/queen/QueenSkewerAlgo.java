package com.agutsul.chess.piece.queen;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.AbstractSkewerLineAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class QueenSkewerAlgo<COLOR extends Color,
                            QUEEN extends QueenPiece<COLOR>>
        extends AbstractSkewerLineAlgo<COLOR,QUEEN> {

    QueenSkewerAlgo(Board board, Algo<QUEEN,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    protected Algo<QUEEN,Collection<Line>> createPieceAlgo(Board board) {
        return new QueenPieceAlgo<>(board);
    }
}