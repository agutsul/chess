package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.LineFactory;

public final class CombinedLineAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR>>
        extends AbstractAlgo<PIECE,Line> {

    private final CompositePieceAlgo<COLOR,PIECE,Line> algo;

    @SuppressWarnings("unchecked")
    public CombinedLineAlgo(Board board) {
        super(board);
        this.algo = new CompositePieceAlgo<>(board,
                new CombinedLineAlgoAdapter<>(board, new HorizontalLineAlgo<>(board)),
                new CombinedLineAlgoAdapter<>(board, new VerticalLineAlgo<>(board)),
                new CombinedDiagonalLineAlgoAdapter<>(board, new DiagonalLineAlgo<>(board))
        );
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        return algo.calculate(piece);
    }

    private static class CombinedLineAlgoAdapter<COLOR extends Color,
                                                 PIECE extends Piece<COLOR>>
            extends AbstractLineAlgo<PIECE,Line> {

        private final AbstractLineAlgo<PIECE,Line> origin;

        public CombinedLineAlgoAdapter(Board board, HorizontalLineAlgo<COLOR,PIECE> algo) {
            this(board, (AbstractLineAlgo<PIECE,Line>) algo);
        }

        public CombinedLineAlgoAdapter(Board board, VerticalLineAlgo<COLOR,PIECE> algo) {
            this(board, (AbstractLineAlgo<PIECE,Line>) algo);
        }

        private CombinedLineAlgoAdapter(Board board, AbstractLineAlgo<PIECE,Line> algo) {
            super(board);
            this.origin = algo;
        }

        @Override
        public Collection<Line> calculate(PIECE piece) {
            return combineLines(piece, origin.calculate(piece));
        }

        protected Collection<Line> combineLines(PIECE piece, Collection<Line> lines) {
            return List.of(createLine(piece, lines));
        }

        protected Line createLine(PIECE piece, Collection<Line> lines) {
            return LineFactory.createLine(piece.getPosition(), lines);
        }
    }

    private static class CombinedDiagonalLineAlgoAdapter<COLOR extends Color,
                                                         PIECE extends Piece<COLOR>>
            extends CombinedLineAlgoAdapter<COLOR,PIECE> {

        public CombinedDiagonalLineAlgoAdapter(Board board,
                                               DiagonalLineAlgo<COLOR,PIECE> algo) {
            super(board, algo);
        }

        @Override
        protected Collection<Line> combineLines(PIECE piece, Collection<Line> lines) {
            var list = new ArrayList<>(lines);

            var line1 = createLine(piece, List.of(list.get(0), list.get(1)));
            var line2 = createLine(piece, List.of(list.get(2), list.get(3)));

            return List.of(line1, line2);
        }
    }
}