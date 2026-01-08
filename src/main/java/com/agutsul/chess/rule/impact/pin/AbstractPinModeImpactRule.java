package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractPinModeImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              PINNED extends Piece<COLOR1> & Pinnable,
                                              PIECE  extends Piece<COLOR1>,
                                              ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                              IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    AbstractPinModeImpactRule(Board board) {
        super(board);
    }

    @Override
    protected Collection<Calculatable> calculate(PINNED piece) {
        return unmodifiableCollection(board.getLines(piece.getPosition()));
    }
}