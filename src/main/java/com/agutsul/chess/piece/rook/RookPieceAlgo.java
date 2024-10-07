package com.agutsul.chess.piece.rook;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.HorizontalLineAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.piece.algo.VerticalLineAlgo;
import com.agutsul.chess.position.Line;

final class RookPieceAlgo<COLOR extends Color,
                          ROOK extends RookPiece<COLOR>>
        extends AbstractAlgo<ROOK, Line>
        implements MovePieceAlgo<COLOR, ROOK, Line>,
                   CapturePieceAlgo<COLOR, ROOK, Line> {

    private final CompositePieceAlgo<COLOR, ROOK, Line> algo;

    @SuppressWarnings("unchecked")
    RookPieceAlgo(Board board) {
        super(board);
        this.algo = new CompositePieceAlgo<COLOR, ROOK, Line>(board,
                        new HorizontalLineAlgo<COLOR, ROOK>(board),
                        new VerticalLineAlgo<COLOR, ROOK>(board)
                    );
    }

    @Override
    public Collection<Line> calculate(ROOK piece) {
        return algo.calculate(piece);
    }
}