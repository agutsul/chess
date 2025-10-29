package com.agutsul.chess.piece.algo;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.util.Collection;

import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class FullLineAlgo<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Lineable>
        implements Algo<PIECE,Collection<Line>> {

    @Override
    public Collection<Line> calculate(PIECE piece) {
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