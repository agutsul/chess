package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public final class PieceCheckPositionImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                ATTACKER extends Piece<COLOR1> & Capturable,
                                                KING extends KingPiece<COLOR2>>
        extends AbstractCheckImpactRule<COLOR1,COLOR2,ATTACKER,KING,
                                        PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>> {

    private final CapturePieceAlgo<COLOR1,ATTACKER,Position> algo;

    public PieceCheckPositionImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(ATTACKER attacker, KING king) {
        var positions = algo.calculate(attacker);
        return positions.contains(king.getPosition())
                ? List.copyOf(positions)
                : emptyList();
    }

    @Override
    protected Collection<PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>>
            createImpacts(ATTACKER attacker, KING king, Collection<Calculated> positions) {

        var impacts = Stream.of(positions)
                .flatMap(Collection::stream)
                .filter(position -> Objects.equals(position, king.getPosition()))
                .map(position -> new PieceCheckImpact<>(attacker, king))
                .collect(toList());

        return impacts;
    }
}