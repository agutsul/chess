package com.agutsul.chess.piece.algo;

import static com.agutsul.chess.line.LineFactory.lineOf;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class HorizontalLineAlgo<COLOR extends Color,
                                      PIECE extends Piece<COLOR>>
        extends AbstractLineAlgo<PIECE,Line> {

    public HorizontalLineAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var currentPosition = piece.getPosition();

        var line1 = lineOf(board, currentPosition,  1, 0);
        var line2 = lineOf(board, currentPosition, -1, 0);

        return List.of(line1, line2);
    }
}