package com.agutsul.chess.rule.impact.pin;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsolutePinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceXRayAttackImpact;
import com.agutsul.chess.activity.impact.PieceXRayImpact;
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

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(board.getKing(piece.getColor()))
                .flatMap(Optional::stream)
                .flatMap(king -> Stream.of(next)
                        .flatMap(Collection::stream)
                        .map(calculated -> (Line) calculated)
                        .filter(line -> line.contains(piece.getPosition()))
                        .filter(line -> line.contains(king.getPosition()))
                        .map(line -> createImpacts(piece, (KING) king, line))
                )
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

        // check if king is x-rayed by line attacker
        var isKingAttacked = Stream.of(board.getImpacts(attacker, Impact.Type.XRAY))
                .flatMap(Collection::stream)
                .map(impact -> (PieceXRayImpact<?,?,?,?>) impact)
                .filter(impact -> impact instanceof PieceXRayAttackImpact<?,?,?,?>)
                .anyMatch(impact -> Objects.equals(impact.getTarget(), defendedPiece));

        return isKingAttacked;
    }

    @Override
    protected PiecePinImpact<COLOR1,COLOR2,PINNED,KING,ATTACKER>
            createImpact(PINNED pinnedPiece, KING defendedPiece, ATTACKER attacker, Line line) {

        return new PieceAbsolutePinImpact<>(pinnedPiece, defendedPiece, attacker, line);
    }
}