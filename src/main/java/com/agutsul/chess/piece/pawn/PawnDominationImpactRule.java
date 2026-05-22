package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.domination.PieceDominationPositionImpactRule;

final class PawnDominationImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends PawnPiece<COLOR1>,
                                     ATTACKED extends Piece<COLOR2>,
                                     IMPACT extends PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceDominationPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo;
    private final Algo<ATTACKER,Collection<Position>> algoAdapter;

    PawnDominationImpactRule(Board board,
                             CapturePieceAlgo<COLOR1,ATTACKER,Position> captureAlgo,
                             EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
        this.algoAdapter = new EnPassantPositionAlgoAdapter<>(enPassantAlgo);
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER pawn) {
        var positions = new LinkedHashSet<>(super.calculate(pawn));
        positions.addAll(algoAdapter.calculate(pawn));
        return unmodifiableCollection(positions);
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER pawn, Collection<Calculatable> next) {
        var attackedPositions = getAttackedPositions(pawn.getColor());

        var enPassantData = Stream.of(enPassantAlgo.calculate(pawn))
                .flatMap(Collection::stream)
                .collect(toMap(EnPassant::getPosition, EnPassant::getPiece));

        @SuppressWarnings("unchecked")
        var enPassantImpacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .filter(position -> enPassantData.containsKey(position))
                .filter(position -> attackedPositions.contains(position))
                .map(position -> {
                    var opponentPawn = enPassantData.get(position);
                    return new PieceDominationAttackImpact<>(
                            new PieceAttackImpact<>(pawn, (ATTACKED) opponentPawn, position)
                    );
                })
                .map(impact -> (IMPACT) impact)
                .toList();

        var captureImpacts = super.createImpacts(pawn, next, attackedPositions);
        return Stream.of(captureImpacts, enPassantImpacts)
                .flatMap(Collection::stream)
                .toList();
    }
}