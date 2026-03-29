package com.agutsul.chess.rule.impact.attack;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.AttackImpactRule;

abstract class AbstractAttackImpactRule<COLOR1 extends Color,
                                        COLOR2 extends Color,
                                        ATTACKER extends Piece<COLOR1> & Capturable,
                                        ATTACKED extends Piece<COLOR2>>
        extends AbstractImpactRule<COLOR1,ATTACKER,
                                   PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        implements AttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    AbstractAttackImpactRule(Board board) {
        super(board, Impact.Type.ATTACK);
    }
}