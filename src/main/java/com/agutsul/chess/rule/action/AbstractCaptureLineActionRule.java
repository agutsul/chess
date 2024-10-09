package com.agutsul.chess.rule.action;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractCaptureLineActionRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    PIECE1 extends Piece<COLOR1> & Capturable,
                                                    PIECE2 extends Piece<COLOR2> & Capturable,
                                                    ACTION extends PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
        extends AbstractCaptureActionRule<COLOR1, COLOR2, PIECE1, PIECE2, ACTION> {

    private final CapturePieceAlgo<COLOR1, PIECE1, Line> algo;

    protected AbstractCaptureLineActionRule(Board board,
                                            CapturePieceAlgo<COLOR1, PIECE1, Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        var lines = algo.calculate(piece);

        var captureLines = new ArrayList<Calculated>();
        for (var line : lines) {
            var capturePositions = new ArrayList<Position>();
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isEmpty()) {
                    capturePositions.add(position);
                    continue;
                }

                var otherPiece = optionalPiece.get();
                if (piece.getColor() != otherPiece.getColor()) {
                    capturePositions.add(position);
                }

                break;
            }

            if (!capturePositions.isEmpty()) {
                captureLines.add(new Line(capturePositions));
            }
        }

        return captureLines;
    }

    @Override
    protected Collection<ACTION> createActions(PIECE1 piece,
                                               Collection<Calculated> calculatedLines) {
        var actions = new ArrayList<ACTION>();
        for (var calculatedLine : calculatedLines) {
            var line = (Line) calculatedLine;

            // get piece on the last position of the line
            var optionalPiece = board.getPiece(line.get(line.size() - 1));
            if (optionalPiece.isPresent()) {
                @SuppressWarnings("unchecked")
                PIECE2 piece2 = (PIECE2) optionalPiece.get();
                actions.add(createAction(piece, piece2, line));
            }
        }

        return actions;
    }

    protected abstract ACTION createAction(PIECE1 piece1, PIECE2 piece2, Line line);
}