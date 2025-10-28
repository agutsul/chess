package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class CaptureLineAlgo<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Capturable>
        extends AbstractAlgo<PIECE,Line>
        implements CapturePieceAlgo<COLOR,PIECE,Line> {

    public enum Mode implements BiPredicate<Piece<?>,Piece<?>> {
        OPPOSITE_COLORS((piece1,piece2) -> !Objects.equals(piece1.getColor(), piece2.getColor())),
        SAME_COLORS((piece1,piece2) -> Objects.equals(piece1.getColor(), piece2.getColor()));

        private BiPredicate<Piece<?>,Piece<?>> predicate;

        Mode(BiPredicate<Piece<?>,Piece<?>> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(Piece<?> piece1, Piece<?> piece2) {
            return predicate.test(piece1, piece2);
        }
    }

    private final Mode mode;
    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public CaptureLineAlgo(Board board,
                           CapturePieceAlgo<COLOR,PIECE,Line> algo) {

        this(Mode.OPPOSITE_COLORS, board, algo);
    }

    public CaptureLineAlgo(Mode mode, Board board,
                           CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.mode = mode;
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var lines = new ArrayList<Line>();
        for (var line : algo.calculate(piece)) {
            var positions = new ArrayList<Position>();
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    if (mode.test(optionalPiece.get(), piece)) {
                        positions.add(position);
                    }

                    break;
                }

                positions.add(position);
            }

            if (!positions.isEmpty()) {
                lines.add(new Line(positions));
            }
        }

        return lines;
    }
}