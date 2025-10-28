package com.agutsul.chess.piece.algo;

import static com.agutsul.chess.line.LineFactory.createLine;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class VerticalLineAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR>>
        extends AbstractLineAlgo<PIECE,Line> {

    public VerticalLineAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var currentPosition = piece.getPosition();

        var line1 = createLine(board, currentPosition, 0, -1);
        var line2 = createLine(board, currentPosition, 0,  1);

        return List.of(line1, line2);
    }
}