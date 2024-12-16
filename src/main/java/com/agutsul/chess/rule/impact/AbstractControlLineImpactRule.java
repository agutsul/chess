package com.agutsul.chess.rule.impact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractControlLineImpactRule<COLOR extends Color,
                                                   PIECE extends Piece<COLOR> & Capturable,
                                                   IMPACT extends PieceControlImpact<COLOR,PIECE>>
        extends AbstractControlImpactRule<COLOR,PIECE,IMPACT> {

    protected final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    protected AbstractControlLineImpactRule(Board board,
                                            CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        var lines = algo.calculate(piece);

        var positions = new ArrayList<Calculated>();
        for (var line : lines) {
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    var foundPiece = optionalPiece.get();
                    if (!Objects.equals(foundPiece.getColor(), piece.getColor())) {
                        positions.add(position);
                    }

                    break;
                }

                positions.add(position);
            }
        }

        return positions;
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece,
                                               Collection<Calculated> positions) {
        var impacts = new ArrayList<IMPACT>();
        for (var position : positions) {
            impacts.add(createImpact(piece, (Position) position));
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE piece, Position position);
}