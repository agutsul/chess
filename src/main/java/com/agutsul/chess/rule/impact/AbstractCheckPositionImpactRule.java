package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;

public abstract class AbstractCheckPositionImpactRule<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      ATTACKER extends Piece<COLOR1> & Capturable,
                                                      KING extends KingPiece<COLOR2>,
                                                      IMPACT extends PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>>
        extends AbstractCheckImpactRule<COLOR1,COLOR2,ATTACKER,KING,IMPACT> {

    protected final CapturePieceAlgo<COLOR1,ATTACKER,Calculated> algo;

    protected AbstractCheckPositionImpactRule(Board board,
                                              CapturePieceAlgo<COLOR1,ATTACKER,Calculated> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(ATTACKER attacker, KING king) {
        var positions = algo.calculate(attacker);
        if (!positions.contains(king.getPosition())) {
            return emptyList();
        }

        return positions;
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER attacker, KING king,
                                               Collection<Calculated> positions) {
        var impacts = new ArrayList<IMPACT>();
        for (var position : positions) {
            if (Objects.equals(position, king.getPosition())) {
                impacts.add(createImpact(attacker, king));
                break;
            }
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(ATTACKER attacker, KING king);
}