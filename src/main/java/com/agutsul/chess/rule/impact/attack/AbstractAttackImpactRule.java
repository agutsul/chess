package com.agutsul.chess.rule.impact.attack;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
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

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        var opponentColor = piece.getColor().invert();

        @SuppressWarnings("unchecked")
        Collection<Calculatable> next = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(opponentPiece -> calculate(piece, (ATTACKED) opponentPiece))
                .flatMap(Collection::stream)
                .collect(toList());

        return next;
    }

    @Override
    protected Collection<PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        var opponentColor = piece.getColor().invert();

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(opponentPiece -> createImpacts(piece, (ATTACKED) opponentPiece, next))
                .flatMap(Collection::stream)
                .collect(toList());

        return impacts;
    }

    protected abstract Collection<Calculatable> calculate(ATTACKER attacker, ATTACKED attacked);

    protected abstract Collection<PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER attacker, ATTACKED attacked, Collection<Calculatable> next);
}