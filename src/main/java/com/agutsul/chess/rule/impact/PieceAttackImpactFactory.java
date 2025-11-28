package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;

import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public abstract class PieceAttackImpactFactory {

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createAttackImpact(ATTACKER predator, ATTACKED victim) {

        var attackImpact = isKing(victim)
                ? new PieceCheckImpact<>(predator, (KingPiece<COLOR2>) victim)
                : new PieceAttackImpact<>(predator, victim);

        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) attackImpact;
    }

    @SuppressWarnings("unchecked")
    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createAttackImpact(ATTACKER predator, ATTACKED victim, Line line) {

        var attackImpact = isKing(victim)
                ? new PieceCheckImpact<>(predator, (KingPiece<COLOR2>) victim, line)
                : new PieceAttackImpact<>(predator, victim, line);

        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) attackImpact;
    }

    public static <COLOR1 extends Color,COLOR2 extends Color,ATTACKER extends Piece<COLOR1> & Capturable,ATTACKED extends Piece<COLOR2>>
            AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> createAttackImpact(ATTACKER predator, ATTACKED victim, Optional<Line> attackLine) {

        return Stream.ofNullable(attackLine)
                .flatMap(Optional::stream)
                .map(line -> createAttackImpact(predator, victim, line))
                .findFirst()
                .orElse(createAttackImpact(predator, victim));
    }
}