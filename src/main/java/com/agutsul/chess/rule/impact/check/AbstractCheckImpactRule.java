package com.agutsul.chess.rule.impact.check;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.CheckImpactRule;

abstract class AbstractCheckImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PIECE extends Piece<COLOR1> & Capturable,
                                       KING extends KingPiece<COLOR2>>
        extends AbstractImpactRule<COLOR1,PIECE,
                                   PieceCheckImpact<COLOR1,COLOR2,PIECE,KING>>
        implements CheckImpactRule<COLOR1,COLOR2,PIECE,KING> {

    AbstractCheckImpactRule(Board board) {
        super(board, Impact.Type.CHECK);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        var opponentColor = piece.getColor().invert();

        @SuppressWarnings("unchecked")
        Collection<Calculatable> next = Stream.of(board.getKing(opponentColor))
                .flatMap(Optional::stream)
                .map(opponentKing -> calculate(piece, (KING) opponentKing))
                .flatMap(Collection::stream)
                .collect(toList());

        return next;
    }

    @Override
    protected Collection<PieceCheckImpact<COLOR1,COLOR2,PIECE,KING>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        var opponentColor = piece.getColor().invert();

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(board.getKing(opponentColor))
                .flatMap(Optional::stream)
                .map(opponentKing -> createImpacts(piece, (KING) opponentKing, next))
                .flatMap(Collection::stream)
                .collect(toList());

        return impacts;
    }

    protected abstract Collection<Calculatable> calculate(PIECE attacker, KING king);

    protected abstract Collection<PieceCheckImpact<COLOR1,COLOR2,PIECE,KING>>
            createImpacts(PIECE attacker, KING king, Collection<Calculatable> next);
}