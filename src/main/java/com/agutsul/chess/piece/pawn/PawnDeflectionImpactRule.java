package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.deflection.PieceDeflectionPositionImpactRule;

final class PawnDeflectionImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends PawnPiece<COLOR1>,
                                     ATTACKED extends Piece<COLOR2>,
                                     DEFENDED extends Piece<COLOR2>>
        extends PieceDeflectionPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED> {

    private final PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo;

    PawnDeflectionImpactRule(Board board,
                             PawnCaptureAlgo<COLOR1,ATTACKER> captureAlgo,
                             PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        var captureImpacts = super.createImpacts(piece, next);

        var enPassantImpacts = Stream.of(enPassantAlgo.calculateData(piece))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .filter(entry -> !Objects.equals(entry.getValue().getColor(), piece.getColor()))
                .map(entry  -> new PieceAttackImpact<>(piece, (ATTACKED) entry.getValue(), entry.getKey()))
                .map(impact -> super.createImpacts(impact))
                .map(impact -> (PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>) impact)
                .collect(toList());

        var impacts = Stream.of(captureImpacts, enPassantImpacts)
                    .flatMap(Collection::stream)
                    .collect(toList());

        return impacts;
    }
}