package com.agutsul.chess.rule.impact.attack;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.DiscoveredAttackImpactRule;

// https://en.wikipedia.org/wiki/Discovered_attack
abstract class AbstractDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  PIECE  extends Piece<COLOR1>,
                                                  ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                  ATTACKED extends Piece<COLOR2>,
                                                  IMPACT extends PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
        extends AbstractImpactRule<COLOR1,PIECE,IMPACT>
        implements DiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,IMPACT> {

    AbstractDiscoveredAttackImpactRule(Board board) {
        super(board, Impact.Type.ATTACK);
    }
}