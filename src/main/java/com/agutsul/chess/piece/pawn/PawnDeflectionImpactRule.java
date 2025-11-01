package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.PieceDeflectionPositionImpactRule;

public final class PawnDeflectionImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            ATTACKER extends PawnPiece<COLOR1>,
                                            ATTACKED extends Piece<COLOR2>,
                                            DEFENDED extends Piece<COLOR2>>
        extends PieceDeflectionPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED> {

    private final PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo;

    public PawnDeflectionImpactRule(Board board,
                                    PawnCaptureAlgo<COLOR1,ATTACKER> captureAlgo,
                                    PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
            createImpacts(ATTACKER piece, Collection<Calculated> next) {

        var captureImpacts = super.createImpacts(piece, next);

        var enPassantImpacts = Stream.of(enPassantAlgo.calculateData(piece))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .filter(attackedPiece -> !Objects.equals(attackedPiece.getColor(), piece.getColor()))
                .map(attackedPiece -> super.createImpacts(
                        createAttackImpact(piece, (ATTACKED) attackedPiece)
                ))
                .map(impact -> (PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>) impact)
                .collect(toList());

        var impacts = Stream.of(captureImpacts, enPassantImpacts)
                    .flatMap(Collection::stream)
                    .collect(toList());

        return impacts;
    }
}