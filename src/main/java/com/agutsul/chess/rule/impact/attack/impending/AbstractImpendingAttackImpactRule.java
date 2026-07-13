package com.agutsul.chess.rule.impact.attack.impending;

import static com.agutsul.chess.color.Colors.isEqual;
import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceRelativeImpendingAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.ImpendingAttackImpactRule;

abstract class AbstractImpendingAttackImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                 ATTACKED extends Piece<COLOR2>,
                                                 IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements ImpendingAttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    AbstractImpendingAttackImpactRule(Board board) {
        super(board, Impact.Type.IMPENDING_ATTACK);
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(ATTACKER piece);

    protected abstract Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next);

    @SuppressWarnings("unchecked")
    protected IMPACT createImpact(ATTACKER attacker, Position position, ATTACKED attacked) {

        var optionalPiece = board.getPiece(position);
        if (optionalPiece.isEmpty()) {
            var impact = isKing(attacked)
                    ? new PieceAbsoluteImpendingAttackImpact<>(attacker, position, (KingPiece<COLOR2>) attacked)
                    : new PieceRelativeImpendingAttackImpact<>(attacker, position, attacked);

            return (IMPACT) impact;
        }

        var foundPiece = optionalPiece.get();
        if (!isEqual(foundPiece.getColor(), attacker.getColor())) {
            var impact = isKing(attacked)
                    ? new PieceAbsoluteImpendingAttackImpact<>(attacker, (Piece<COLOR2>) foundPiece, (KingPiece<COLOR2>) attacked)
                    : new PieceRelativeImpendingAttackImpact<>(attacker, (Piece<COLOR2>) foundPiece, attacked);

            return (IMPACT) impact;
        }

        return null;
    }
}