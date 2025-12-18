package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.fork.PieceForkPositionImpactRule;

final class PawnForkImpactRule<COLOR1 extends Color,
                               COLOR2 extends Color,
                               ATTACKER extends PawnPiece<COLOR1>,
                               ATTACKED extends Piece<COLOR2>>
        extends PieceForkPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo;

    PawnForkImpactRule(Board board,
                       PawnCaptureAlgo<COLOR1,ATTACKER> captureAlgo,
                       PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createAttackImpacts(ATTACKER pawn, Collection<Calculatable> next) {

        var impacts = new ArrayList<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>();

        // add all usual pawn capture impacts
        impacts.addAll(super.createAttackImpacts(pawn, next));

        // add en-passante impacts
        @SuppressWarnings("unchecked")
        var enPassantAttackImpacts = Stream.of(enPassantAlgo.calculateData(pawn))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(entry  -> new PieceAttackImpact<>(pawn, (ATTACKED) entry.getValue(), entry.getKey()))
                .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact)
                .collect(toList());

        impacts.addAll(enPassantAttackImpacts);

        return impacts;
    }
}