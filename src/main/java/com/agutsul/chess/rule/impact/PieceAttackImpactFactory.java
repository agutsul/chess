package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;

import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceAbsoluteImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceRelativeImpendingAttackImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class PieceAttackImpactFactory {

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createAttackImpact(ATTACKER predator, ATTACKED victim) {

        var impact = isKing(victim)
                ? new PieceCheckImpact<>(predator, (KingPiece<COLOR2>) victim)
                : new PieceAttackImpact<>(predator, victim);

        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact;
    }

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createAttackImpact(ATTACKER predator, ATTACKED victim, Line line) {

        var impact = isKing(victim)
                ? new PieceCheckImpact<>(predator, (KingPiece<COLOR2>) victim, line)
                : new PieceAttackImpact<>(predator, victim, line);

        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact;
    }

    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createAttackImpact(ATTACKER predator, ATTACKED victim, Optional<Line> attackLine) {

        return Stream.ofNullable(attackLine)
                .flatMap(Optional::stream)
                .map(line -> createAttackImpact(predator, victim, line))
                .findFirst()
                .orElse(createAttackImpact(predator, victim));
    }

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createHiddenAttackImpact(ATTACKER predator, ATTACKED victim, Line line) {

        var impact = isKing(victim)
                ? new PieceCheckImpact<>(predator, (KingPiece<COLOR2>) victim, line, true)
                : new PieceAttackImpact<>(predator, victim, line, true);

        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact;
    }

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Movable & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createImpendingAttackImpact(ATTACKER predator, Position position, ATTACKED victim) {

        var impendingImpact = isKing(victim)
                ? new PieceAbsoluteImpendingAttackImpact<>(predator, position, (KingPiece<COLOR2>) victim)
                : new PieceRelativeImpendingAttackImpact<>(predator, position, victim);

        var impact = new PieceImpendingAttackImpactAdapter<>(impendingImpact);
        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact;
    }

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Movable & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createImpendingAttackImpact(ATTACKER predator, Piece<COLOR2> victim, ATTACKED nextVictim) {

        var impendingImpact = isKing(nextVictim)
                ? new PieceAbsoluteImpendingAttackImpact<>(predator, victim, (KingPiece<COLOR2>) nextVictim)
                : new PieceRelativeImpendingAttackImpact<>(predator, victim, nextVictim);

        var impact = new PieceImpendingAttackImpactAdapter<>(impendingImpact);
        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact;
    }

    private static final class PieceImpendingAttackImpactAdapter<COLOR1 extends Color,
                                                                 COLOR2 extends Color,
                                                                 ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                                 ATTACKED extends Piece<COLOR2>>
            extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

        private final PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> impact;

        private PieceImpendingAttackImpactAdapter(PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> impact) {
            super(impact.getType(), impact.getAttacker(), impact.getAttacked(), null, false);
            this.impact = impact;
        }

        @Override
        public Position getPosition() {
            return this.impact.getPosition();
        }

        @Override
        public Integer getValue() {
            return this.impact.getValue();
        }

        @Override
        public String toString() {
            return String.valueOf(this.impact);
        }
    }
}