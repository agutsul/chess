package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class PinPieceAlgo<COLOR extends Color,
                                PIECE extends Piece<COLOR>>
        extends AbstractAlgo<PIECE,Line> {

    private final CompositePieceAlgo<COLOR,PIECE,Line> algo;

    @SuppressWarnings("unchecked")
    public PinPieceAlgo(Board board) {
        super(board);
        this.algo = new CompositePieceAlgo<>(board,
                        new PinLineAlgo<>(board, new HorizontalLineAlgo<>(board)),
                        new PinLineAlgo<>(board, new VerticalLineAlgo<>(board)),
                        new PinDiagonalLineAlgo<>(board, new DiagonalLineAlgo<>(board))
                    );
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        return algo.calculate(piece);
    }

    private static class PinLineAlgo<COLOR extends Color,
                                     PIECE extends Piece<COLOR>>
            extends AbstractLineAlgo<PIECE,Line> {

        private final AbstractLineAlgo<PIECE,Line> origin;

        public PinLineAlgo(Board board, HorizontalLineAlgo<COLOR,PIECE> algo) {
            this(board, (AbstractLineAlgo<PIECE,Line>) algo);
        }

        public PinLineAlgo(Board board, VerticalLineAlgo<COLOR,PIECE> algo) {
            this(board, (AbstractLineAlgo<PIECE,Line>) algo);
        }

        private PinLineAlgo(Board board, AbstractLineAlgo<PIECE,Line> algo) {
            super(board);
            this.origin = algo;
        }

        @Override
        public Collection<Line> calculate(PIECE piece) {
            return process(new ArrayList<>(origin.calculate(piece)));
        }

        protected Collection<Line> process(List<Line> lines) {
            return List.of(new Line(lines.get(0), lines.get(1)));
        }
    }

    private static class PinDiagonalLineAlgo<COLOR extends Color,
                                             PIECE extends Piece<COLOR>>
            extends PinLineAlgo<COLOR,PIECE> {

        public PinDiagonalLineAlgo(Board board,
                                   DiagonalLineAlgo<COLOR, PIECE> algo) {
            super(board, algo);
        }

        @Override
        protected Collection<Line> process(List<Line> lines) {
            var line1 = new Line(lines.get(0), lines.get(1));
            var line2 = new Line(lines.get(2), lines.get(3));

            return List.of(line1, line2);
        }
    }
}