package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.impact.PieceUnderminingPositionImpactRule;

final class PawnUnderminingImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends PawnPiece<COLOR1>,
                                      ATTACKED extends Piece<COLOR2>>
        extends PieceUnderminingPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo;

    PawnUnderminingImpactRule(Board board,
                              PawnCaptureAlgo<COLOR1,ATTACKER> captureAlgo,
                              PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER pawn, Collection<Calculated> next) {

        var impacts = new ArrayList<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>();
        impacts.addAll(super.createImpacts(pawn, next));

        var enPassantUnderminingImpacts = Stream.of(enPassantAlgo.calculateData(pawn))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .map(opponentPawn -> super.createImpacts(pawn, List.of(opponentPawn.getPosition())))
                .flatMap(Collection::stream)
                .collect(toList());

        impacts.addAll(enPassantUnderminingImpacts);

        return impacts;
    }
}