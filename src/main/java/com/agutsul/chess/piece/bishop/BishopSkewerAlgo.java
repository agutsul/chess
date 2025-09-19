package com.agutsul.chess.piece.bishop;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.algo.AbstractSkewerLineAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class BishopSkewerAlgo<COLOR extends Color,
                             BISHOP extends BishopPiece<COLOR>>
        extends AbstractSkewerLineAlgo<COLOR,BISHOP> {

    BishopSkewerAlgo(Board board, Algo<BISHOP,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    protected Algo<BISHOP,Collection<Line>> createPieceAlgo(Board board) {
        return new BishopPieceAlgo<>(board);
    }
}