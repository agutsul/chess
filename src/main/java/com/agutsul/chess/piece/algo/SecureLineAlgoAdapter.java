package com.agutsul.chess.piece.algo;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.line.LineBuilder;
import com.agutsul.chess.piece.Piece;

public final class SecureLineAlgoAdapter<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable & Lineable>
        extends AbstractLineAlgo<PIECE,Line>
        implements CapturePieceAlgo<COLOR,PIECE,Line> {

    public enum Mode implements BiPredicate<Piece<?>,Piece<?>> {
        OPPOSITE_COLORS((color1,color2) -> !Objects.equals(color1, color2)),
        SAME_COLORS((color1,color2)     ->  Objects.equals(color1, color2));

        private BiPredicate<Color,Color> predicate;

        Mode(BiPredicate<Color,Color> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(Piece<?> piece1, Piece<?> piece2) {
            return predicate.test(piece1.getColor(), piece2.getColor());
        }
    }

    private final Mode mode;
    private final Algo<PIECE,Collection<Line>> algo;

    public SecureLineAlgoAdapter(Mode mode, Board board,
                                 CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.mode = mode;
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var lines = new ArrayList<Line>();

        var lineBuilder = new LineBuilder();
        for (var line : algo.calculate(piece)) {
            lineBuilder.reset();

            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    if (mode.test(optionalPiece.get(), piece)) {
                        lineBuilder.append(position);
                    }

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