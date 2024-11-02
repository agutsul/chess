package com.agutsul.chess.rule.impact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractMonitorLineImpactRule<COLOR extends Color,
                                                    PIECE extends Piece<COLOR> & Capturable,
                                                    IMPACT extends PieceMonitorImpact<COLOR, PIECE>>
        extends AbstractMonitorImpactRule<COLOR, PIECE, IMPACT> {

    private final CapturePieceAlgo<COLOR, PIECE, Line> algo;

    protected AbstractMonitorLineImpactRule(Board board,
                                            CapturePieceAlgo<COLOR, PIECE, Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        var lines = algo.calculate(piece);

        var monitorLines = new ArrayList<Calculated>();
        for (var line : lines) {
            var monitorPositions = new ArrayList<Position>();

            var isPieceFound = false;

            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (!isPieceFound && optionalPiece.isEmpty()) {
                    continue;
                }

                if (!isPieceFound) {
                    isPieceFound = true;
                    continue;
                }

                monitorPositions.add(position);
            }

            if (!monitorPositions.isEmpty()) {
                monitorLines.add(new Line(monitorPositions));
            }
        }

        return monitorLines;
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece,
                                               Collection<Calculated> calculatedLines) {

        var impacts = new ArrayList<IMPACT>();
        for (var line : calculatedLines) {
            @SuppressWarnings("unchecked")
            var positions = (List<Position>) line;
            for (var position : positions) {
                impacts.add(createImpact(piece, position));
            }
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE piece, Position position);
}