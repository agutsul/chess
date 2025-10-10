package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public CaptureLineAlgo(Board board,
                           CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var captureLines = new ArrayList<Line>();
        for (var line : algo.calculate(piece)) {
            var capturePositions = new ArrayList<Position>();
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    var attackedPiece = optionalPiece.get();
                    if (!Objects.equals(attackedPiece.getColor(), piece.getColor())) {
                        capturePositions.add(position);
                    }

                    break;
                }

                capturePositions.add(position);
            }

            if (!capturePositions.isEmpty()) {
                captureLines.add(new Line(capturePositions));
            }
        }

        return captureLines;
    }
}