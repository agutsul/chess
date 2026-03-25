package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsolutePinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class PieceAbsolutePinImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable,
                                       KING   extends KingPiece<COLOR1>,
                                       ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends AbstractPinModeImpactRule<COLOR1,COLOR2,PINNED,KING,ATTACKER,
                                          PiecePinImpact<COLOR1,COLOR2,PINNED,KING,ATTACKER>> {

    PieceAbsolutePinImpactRule(Board board) {
        super(board);
    }

    @Override
    protected Collection<PiecePinImpact<COLOR1,COLOR2,PINNED,KING,ATTACKER>>
            createImpacts(PINNED piece, Collection<Calculatable> next) {

        var optionalKing = board.getKing(piece.getColor());
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        @SuppressWarnings("unchecked")
        var king = (KING) optionalKing.get();

        var impactLines = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .filter(line -> line.contains(king.getPosition()))
                .filter(line -> line.contains(piece.getPosition()))
                .toList();

        if (impactLines.isEmpty()) {
            return emptyList();
        }

        var impacts = Stream.of(impactLines)
                .flatMap(Collection::stream)
                .map(line -> createImpacts(piece, king, line))
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        return impacts;
    }

    @Override
    protected boolean isAttacked(ATTACKER attacker, PINNED pinnedPiece, KING defendedPiece) {
        if (!super.isAttacked(attacker, pinnedPiece, defendedPiece)) {
            return false;
        }

        // check if king is monitored by line attacker
        var isKingMonitored = Stream.of(board.getImpacts(attacker, Impact.Type.MONITOR))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .anyMatch(position -> Objects.equals(position, defendedPiece.getPosition()));

        return isKingMonitored;
    }

    @Override
    protected PiecePinImpact<COLOR1,COLOR2,PINNED,KING,ATTACKER>
            createImpact(PINNED pinnedPiece, KING defendedPiece, ATTACKER attacker, Line line) {

        return new PieceAbsolutePinImpact<>(pinnedPiece, defendedPiece, attacker, line);
    }
}