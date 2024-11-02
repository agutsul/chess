package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PiecePinImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface PinImpactRule<COLOR1 extends Color,
                               COLOR2 extends Color,
                               PIECE extends Piece<COLOR1>,
                               KING extends KingPiece<COLOR1>,
                               ATTACKER extends Piece<COLOR2> & Capturable,
                               IMPACT extends PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>>
        extends Rule<PIECE, Collection<IMPACT>> {

}