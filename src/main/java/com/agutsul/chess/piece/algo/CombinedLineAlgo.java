package com.agutsul.chess.piece.algo;

import static com.agutsul.chess.line.LineFactory.lineOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.line.LineBuilder;
import com.agutsul.chess.piece.Piece;

public final class CombinedLineAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Lineable>
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
                                                 PIECE extends Piece<COLOR> & Lineable>
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
            var list = new ArrayList<>(lines);

            var subLine1 = createSubLine(piece, list.getFirst());
            var subLine2 = createSubLine(piece, list.getLast());

            return List.of(lineOf(subLine1, subLine2));
        }

        private Line createSubLine(PIECE piece, Line line) {
            return new LineBuilder()
                    .append(piece.getPosition())
                    .append(line)
                    .sort()
                    .build();
        }
    }

    private static class CombinedDiagonalLineAlgoAdapter<COLOR extends Color,
                                                         PIECE extends Piece<COLOR> & Lineable>
            extends CombinedLineAlgoAdapter<COLOR,PIECE> {

        public CombinedDiagonalLineAlgoAdapter(Board board,
                                               DiagonalLineAlgo<COLOR,PIECE> algo) {
            super(board, algo);
        }

        @Override
        protected Collection<Line> combineLines(PIECE piece, Collection<Line> lines) {
            var list = new ArrayList<>(lines);

            var diagonal1 = super.combineLines(piece, list.subList(0,2));
            var diagonal2 = super.combineLines(piece, list.subList(2,4));

            return Stream.of(diagonal1, diagonal2)
                    .flatMap(Collection::stream)
                    .toList();
        }
    }
}