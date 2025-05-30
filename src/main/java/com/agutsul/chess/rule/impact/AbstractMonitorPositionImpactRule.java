package com.agutsul.chess.rule.impact;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

@Deprecated
public abstract class AbstractMonitorPositionImpactRule<COLOR extends Color,
                                                        PIECE extends Piece<COLOR> & Capturable,
                                                        IMPACT extends PieceMonitorImpact<COLOR,PIECE>>
        extends AbstractMonitorImpactRule<COLOR,PIECE,IMPACT> {

    protected final CapturePieceAlgo<COLOR,PIECE,Calculated> algo;

    protected AbstractMonitorPositionImpactRule(Board board,
                                                CapturePieceAlgo<COLOR,PIECE,Calculated> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        return algo.calculate(piece);
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculated> next) {
        var impacts = new ArrayList<IMPACT>();
        for (var entry : next) {
            var position = (Position) entry;
            if (board.isEmpty(position)) {
                impacts.add(createImpact(piece, position));
            }
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE piece, Position position);
}