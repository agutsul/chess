package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.deflection.PieceDeflectionPositionImpactRule;

final class PawnDeflectionImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends PawnPiece<COLOR1>,
                                     ATTACKED extends Piece<COLOR2>,
                                     DEFENDED extends Piece<COLOR2>>
        extends PieceDeflectionPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED> {

    private final EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo;
    private final Algo<ATTACKER,Collection<Position>> algoAdapter;

    PawnDeflectionImpactRule(Board board,
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
    @SuppressWarnings("unchecked")
    protected Collection<PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        var captureImpacts = super.createImpacts(piece, next);

        var enPassantImpacts = Stream.of(enPassantAlgo.calculate(piece))
                .flatMap(Collection::parallelStream)
                .map(enPassant -> new PieceAttackImpact<>(piece, (ATTACKED) enPassant.getPiece(), enPassant.getPosition()))
                .map(impact -> super.createImpacts(impact))
                .flatMap(Collection::parallelStream)
                .toList();

        return Stream.of(captureImpacts, enPassantImpacts)
                    .flatMap(Collection::parallelStream)
                    .toList();
    }
}