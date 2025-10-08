package com.agutsul.chess.piece.algo;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class SkewerLineAlgo<COLOR extends Color,
                                  PIECE extends Piece<COLOR>>
        extends AbstractAlgo<PIECE,Line> {

    private final Algo<PIECE,Collection<Line>> algo;

    public SkewerLineAlgo(Board board, Algo<PIECE,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var pieceLines = algo.calculate(piece);

        var lines = new ArrayList<Line>();
        for (var fullLine : calculateFullLines(piece)) {
            for (var pieceLine : pieceLines) {
                if (fullLine.containsAll(pieceLine) && !lines.contains(fullLine)) {
                    lines.add(fullLine);
                    break;
                }
            }
        }

        return lines;
    }

    private Collection<Line> calculateFullLines(PIECE piece) {
        var singlePieceBoard = new PositionedBoardBuilder()
                .withPiece(piece.getType(), piece.getColor(), piece.getPosition())
                .build();

        var fullLineAlgo = new CombinedLineAlgo<>(singlePieceBoard);
        try {
            var tmpPiece = singlePieceBoard.getPiece(piece.getPosition());
            return fullLineAlgo.calculate(tmpPiece.get());
        } finally {
            closeQuietly((Closeable) singlePieceBoard);
        }
    }
}