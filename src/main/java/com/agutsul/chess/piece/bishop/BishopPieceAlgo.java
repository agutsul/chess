package com.agutsul.chess.piece.bishop;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.DiagonalLineAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;

final class BishopPieceAlgo<COLOR extends Color,
                            BISHOP extends BishopPiece<COLOR>>
        extends AbstractAlgo<BISHOP,Line>
        implements MovePieceAlgo<COLOR,BISHOP,Line>,
                   CapturePieceAlgo<COLOR,BISHOP,Line> {

    private final DiagonalLineAlgo<COLOR,BISHOP> algo;

    BishopPieceAlgo(Board board) {
        super(board);
        this.algo = new DiagonalLineAlgo<>(board);
    }

    @Override
    public Collection<Line> calculate(BISHOP piece) {
        return algo.calculate(piece);
    }
}