package com.agutsul.chess.rule.impact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractProtectLineImpactRule<COLOR extends Color,
                                                    PIECE1 extends Piece<COLOR> & Capturable,
                                                    PIECE2 extends Piece<COLOR>,
                                                    IMPACT extends PieceProtectImpact<COLOR,PIECE1,PIECE2>>
        extends AbstractProtectImpactRule<COLOR,PIECE1,PIECE2,IMPACT> {

    protected final CapturePieceAlgo<COLOR,PIECE1,Line> algo;

    protected AbstractProtectLineImpactRule(Board board,
                                            CapturePieceAlgo<COLOR,PIECE1,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        var lines = algo.calculate(piece);

        var protectLines = new ArrayList<Calculated>();
        for (var line : lines) {
            var protectPositions = new ArrayList<Position>();
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isEmpty()) {
                    continue;
                }

                var otherPiece = optionalPiece.get();
                if (!Objects.equals(piece.getColor(), otherPiece.getColor())) {
                    break;
                }

                protectPositions.add(position);
            }

            if (!protectPositions.isEmpty()) {
                protectLines.add(new Line(protectPositions));
            }
        }

        return protectLines;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<IMPACT> createImpacts(PIECE1 piece,
                                               Collection<Calculated> calculatedLines) {
        var impacts = new ArrayList<IMPACT>();
        for (var line : calculatedLines) {

            var positions = (List<Position>) line;
            for (var position : positions) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    var piece2 = (PIECE2) optionalPiece.get();
                    impacts.add(createImpact(piece, piece2));
                }
            }
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE1 piece1, PIECE2 piece2);
}