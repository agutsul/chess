package com.agutsul.chess.piece.algo;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

public abstract class AbstractSkewerLineAlgo<COLOR extends Color,
                                             PIECE extends Piece<COLOR>>
        extends AbstractAlgo<PIECE,Line> {

    private static final Comparator<Position> COMPARATOR = new PositionComparator();

    private final Algo<PIECE,Collection<Line>> algo;

    protected AbstractSkewerLineAlgo(Board board, Algo<PIECE,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var lines = new LinkedHashSet<Line>();

        var pieceLines = algo.calculate(piece);
        var allLines = calculateAllLines(piece);

        for (var line : allLines) {
            for (var pieceLine : pieceLines) {
                if (line.containsAll(pieceLine)) {
                    var positions = new ArrayList<Position>();

                    positions.add(piece.getPosition());
                    positions.addAll(line);

                    sort(positions, COMPARATOR);

                    lines.add(new Line(positions));
                    break;
                }
            }
        }

        return lines;
    }

    protected Collection<Line> calculateAllLines(PIECE piece) {
        var singlePieceBoard = new PositionedBoardBuilder()
                .withPiece(piece.getType(), piece.getColor(), piece.getPosition())
                .build();

        var allLinesAlgo = createPieceAlgo(singlePieceBoard);

        @SuppressWarnings("unchecked")
        var tmpPiece = (PIECE) singlePieceBoard.getPiece(piece.getPosition()).get();
        return allLinesAlgo.calculate(tmpPiece);
    }

    protected abstract Algo<PIECE,Collection<Line>> createPieceAlgo(Board board);
}