package com.agutsul.chess.piece.algo;

import static com.agutsul.chess.position.LineFactory.createLine;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class MoveLineAlgoAdapter<COLOR extends Color,
                                       PIECE extends Piece<COLOR> & Movable>
        extends AbstractLineAlgo<PIECE,Line>
        implements MovePieceAlgo<COLOR,PIECE,Line> {

    private final MovePieceAlgo<COLOR,PIECE,Line> algo;

    public MoveLineAlgoAdapter(Board board,
                               MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var lines = new ArrayList<Line>();
        for (var line : algo.calculate(piece)) {
            var positions = new ArrayList<Position>();
            for (var position : line) {
                if (!board.isEmpty(position)) {
                    break;
                }

                positions.add(position);
            }

            if (!positions.isEmpty()) {
                lines.add(createLine(positions));
            }
        }

        return lines;
    }
}