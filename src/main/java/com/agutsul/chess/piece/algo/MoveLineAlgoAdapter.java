package com.agutsul.chess.piece.algo;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.line.LineBuilder;
import com.agutsul.chess.piece.Piece;

public final class MoveLineAlgoAdapter<COLOR extends Color,
                                       PIECE extends Piece<COLOR> & Movable & Lineable>
        extends AbstractLineAlgo<PIECE,Line>
        implements MovePieceAlgo<COLOR,PIECE,Line> {

    private final Algo<PIECE,Collection<Line>> algo;

    public MoveLineAlgoAdapter(Board board,
                               MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var lines = new ArrayList<Line>();

        var lineBuilder = new LineBuilder();
        for (var line : algo.calculate(piece)) {
            lineBuilder.reset();

            for (var position : line) {
                if (!board.isEmpty(position)) {
                    break;
                }

                lineBuilder.append(position);
            }

            if (lineBuilder.isReady()) {
                lines.add(lineBuilder.build());
            }
        }

        return unmodifiableList(lines);
    }
}