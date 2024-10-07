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

public abstract class AbstractCaptureLineActionRule<C1 extends Color,
                                                    C2 extends Color,
                                                    P1 extends Piece<C1> & Capturable,
                                                    P2 extends Piece<C2> & Capturable,
                                                    A extends PieceCaptureAction<C1,C2,P1,P2>>
        extends AbstractCaptureActionRule<C1, C2, P1, P2, A> {

    private final CapturePieceAlgo<C1, P1, Line> algo;

    protected AbstractCaptureLineActionRule(Board board,
                                            CapturePieceAlgo<C1, P1, Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(P1 piece) {
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
    protected Collection<A> createActions(P1 piece, Collection<Calculated> calculatedLines) {
        var actions = new ArrayList<A>();
        for (var calculatedLine : calculatedLines) {
            var line = (Line) calculatedLine;

            // get piece on the last position of the line
            var optionalPiece = board.getPiece(line.get(line.size() - 1));
            if (optionalPiece.isPresent()) {
                @SuppressWarnings("unchecked")
                var piece2 = (P2) optionalPiece.get();
                actions.add(createAction(piece, piece2, line));
            }
        }

        return actions;
    }

    protected abstract A createAction(P1 piece1, P2 piece2, Line line);
}