package com.agutsul.chess.piece.queen;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.DiagonalLineAlgo;
import com.agutsul.chess.piece.algo.HorizontalLineAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.piece.algo.VerticalLineAlgo;
import com.agutsul.chess.position.Line;

final class QueenPieceAlgo<COLOR extends Color,
                           QUEEN extends QueenPiece<COLOR>>
        extends AbstractAlgo<QUEEN, Line>
        implements MovePieceAlgo<COLOR, QUEEN, Line>,
                   CapturePieceAlgo<COLOR, QUEEN, Line> {

    private final CompositePieceAlgo<COLOR, QUEEN, Line> algo;

    @SuppressWarnings("unchecked")
    QueenPieceAlgo(Board board) {
        super(board);
        this.algo = new CompositePieceAlgo<>(board,
                        new HorizontalLineAlgo<>(board),
                        new VerticalLineAlgo<>(board),
                        new DiagonalLineAlgo<>(board)
        );
    }

    @Override
    public Collection<Line> calculate(QUEEN piece) {
        return algo.calculate(piece);
    }
}