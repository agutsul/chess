package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.attack.PieceAttackPositionImpactRule;

final class PawnAttackImpactRule<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 ATTACKER extends PawnPiece<COLOR1>,
                                 ATTACKED extends Piece<COLOR2>>
        extends PieceAttackPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo;
    private final Algo<ATTACKER,Collection<Position>> algoAdapter;

    PawnAttackImpactRule(Board board,
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
    protected Collection<PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER attacker, Collection<Calculatable> next) {

        var captureAttackImpacts = super.createImpacts(attacker, next);

        var enPassantData = Stream.of(enPassantAlgo.calculate(attacker))
                .flatMap(Collection::parallelStream)
                .collect(toMap(EnPassant::getPosition, EnPassant::getPiece));

        if (enPassantData.isEmpty()) {
            return captureAttackImpacts;
        }

        var impacts = new ArrayList<>(captureAttackImpacts);

        @SuppressWarnings("unchecked")
        var enPassantAttackImpacts = Stream.of(next)
                .flatMap(Collection::parallelStream)
                .map(calculated -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .filter(position -> enPassantData.containsKey(position))
                .map(position -> {
                    var opponentPawn = (ATTACKED) enPassantData.get(position);
                    return new PieceAttackImpact<>(attacker, opponentPawn, position);
                })
                .toList();

        impacts.addAll(enPassantAttackImpacts);

        return unmodifiableCollection(impacts);
    }
}