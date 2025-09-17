package com.agutsul.chess.piece.algo;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

public final class PinLineAlgo<COLOR extends Color,
                               PIECE extends Piece<COLOR>>
        extends AbstractAlgo<PIECE,Line> {

    private final CompositePieceAlgo<COLOR,PIECE,Line> algo;

    @SuppressWarnings("unchecked")
    public PinLineAlgo(Board board) {
        super(board);
        this.algo = new CompositePieceAlgo<>(board,
                new PinLineAlgoAdapter<>(board, new HorizontalLineAlgo<>(board)),
                new PinLineAlgoAdapter<>(board, new VerticalLineAlgo<>(board)),
                new PinDiagonalLineAlgoAdapter<>(board, new DiagonalLineAlgo<>(board))
        );
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        return algo.calculate(piece);
    }

    private static class PinLineAlgoAdapter<COLOR extends Color,
                                            PIECE extends Piece<COLOR>>
            extends AbstractLineAlgo<PIECE,Line> {

        private static final Comparator<Position> COMPARATOR = new PositionComparator();

        private final AbstractLineAlgo<PIECE,Line> origin;

        public PinLineAlgoAdapter(Board board, HorizontalLineAlgo<COLOR,PIECE> algo) {
            this(board, (AbstractLineAlgo<PIECE,Line>) algo);
        }

        public PinLineAlgoAdapter(Board board, VerticalLineAlgo<COLOR,PIECE> algo) {
            this(board, (AbstractLineAlgo<PIECE,Line>) algo);
        }

        private PinLineAlgoAdapter(Board board, AbstractLineAlgo<PIECE,Line> algo) {
            super(board);
            this.origin = algo;
        }

        @Override
        public Collection<Line> calculate(PIECE piece) {
            return process(piece, origin.calculate(piece));
        }

        protected Collection<Line> process(PIECE piece, Collection<Line> lines) {
            return List.of(createLine(piece, lines));
        }

        protected Line createLine(PIECE piece, Collection<Line> lines) {
            var positions = new ArrayList<Position>();
            positions.add(piece.getPosition());

            lines.forEach(line -> positions.addAll(line));

            sort(positions, COMPARATOR);
            return new Line(positions);
        }
    }

    private static class PinDiagonalLineAlgoAdapter<COLOR extends Color,
                                             PIECE extends Piece<COLOR>>
            extends PinLineAlgoAdapter<COLOR,PIECE> {

        public PinDiagonalLineAlgoAdapter(Board board,
                                   DiagonalLineAlgo<COLOR, PIECE> algo) {
            super(board, algo);
        }

        @Override
        protected Collection<Line> process(PIECE piece, Collection<Line> lines) {
            var list = new ArrayList<>(lines);

            var line1 = createLine(piece, List.of(list.get(0), list.get(1)));
            var line2 = createLine(piece, List.of(list.get(2), list.get(3)));

            return List.of(line1, line2);
        }
    }
}