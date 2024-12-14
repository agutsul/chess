package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PiecePinImpact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractPinImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     PIECE extends Piece<COLOR1>,
                                     KING extends KingPiece<COLOR1>,
                                     ATTACKER extends Piece<COLOR2> & Capturable,
                                     IMPACT extends PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements PinImpactRule<COLOR1,COLOR2,PIECE,KING,ATTACKER,IMPACT> {

    protected AbstractPinImpactRule(Board board) {
        super(board, Impact.Type.PIN);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE piece) {
        var lines = calculate(piece);
        if (lines.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, lines);
    }

    protected abstract Collection<Line> calculate(PIECE piece);

    protected abstract Collection<IMPACT> createImpacts(PIECE piece, Collection<Line> lines);
}