package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceAbsoluteImpendingAttackImpact<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                      ATTACKED extends KingPiece<COLOR2>,
                                                      SOURCE extends AbstractTargetActivity<Impact.Type,ATTACKER,?> & Impact<ATTACKER>>
        extends AbstractPieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,SOURCE,
                                                   PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    @SuppressWarnings("unchecked")
    public PieceAbsoluteImpendingAttackImpact(ATTACKER attacker, Position position, ATTACKED attacked) {
        this((SOURCE) new PieceMotionImpact<>(attacker, position),
                new PieceCheckImpact<>(attacker, attacked)
        );
    }

    @SuppressWarnings("unchecked")
    public PieceAbsoluteImpendingAttackImpact(ATTACKER attacker, Piece<COLOR2> attacked, ATTACKED nextAttacked) {
        this((SOURCE) createAttackImpact(attacker, attacked),
                new PieceCheckImpact<>(attacker, nextAttacked)
        );
    }

    public PieceAbsoluteImpendingAttackImpact(SOURCE sourceImpact,
                                              PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> targetImpact) {

        super(Mode.ABSOLUTE, sourceImpact, targetImpact);
    }

    @Override
    Integer calculateValue() {
        var attacker = getTarget().getSource();
        var attacked = getTarget().getTarget();

        var diff = attacker.getDirection()
                * (Math.abs(attacked.getValue()) - Math.abs(attacker.getValue()));

        var board = attacker.getBoard();
        var isProtected = Stream.of(board.getPieces(attacker.getColor()))
                .flatMap(Collection::stream)
                .filter(piece -> !Objects.equals(attacker, piece))
                .map(piece -> board.getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .anyMatch(position -> Objects.equals(position, getPosition()));

        var value = Math.negateExact(attacked.getValue());
        var targetValue = isProtected
                ? value + diff
                : value/2 + diff; // decrease check value for unprotected piece

        return getSource().getValue() + targetValue;
    }
}