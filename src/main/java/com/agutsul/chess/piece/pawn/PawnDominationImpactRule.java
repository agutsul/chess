package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.domination.PieceDominationPositionImpactRule;

final class PawnDominationImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends PawnPiece<COLOR1>,
                                     ATTACKED extends Piece<COLOR2>,
                                     IMPACT extends PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceDominationPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo;

    PawnDominationImpactRule(Board board,
                             PawnCaptureAlgo<COLOR1,ATTACKER> captureAlgo,
                             PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER pawn) {
        var positions = new ArrayList<>(super.calculate(pawn));
        positions.addAll(enPassantAlgo.calculate(pawn));
        return unmodifiableCollection(positions);
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER pawn, Collection<Calculatable> next) {
        var impacts = new ArrayList<IMPACT>();

        var attackedPositions = getAttackedPositions(pawn.getColor());
        impacts.addAll(super.createImpacts(pawn, next, attackedPositions));

        var enPassantData = enPassantAlgo.calculateData(pawn);

        @SuppressWarnings("unchecked")
        var enPassantImpacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .filter(position -> enPassantData.containsKey(position))
                .filter(position -> attackedPositions.contains(position))
                .map(position -> Stream.of(enPassantData.get(position))
                        .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), pawn.getColor()))
                        .filter(Piece::isPawn)
                        .map(opponentPawn -> new PieceAttackImpact<>(pawn, (ATTACKED) opponentPawn, position)) // enPassant attack
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .map(impact -> new PieceDominationAttackImpact<>(impact))
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        impacts.addAll(enPassantImpacts);

        return unmodifiableCollection(impacts);
    }
}