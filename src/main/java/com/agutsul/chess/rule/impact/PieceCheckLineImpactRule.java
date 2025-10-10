package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CaptureLineAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;

public final class PieceCheckLineImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            ATTACKER extends Piece<COLOR1> & Capturable,
                                            KING extends KingPiece<COLOR2>>
        extends AbstractCheckImpactRule<COLOR1,COLOR2,ATTACKER,KING,
                                        PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>> {

    private final CapturePieceAlgo<COLOR1,ATTACKER,Line> algo;

    public PieceCheckLineImpactRule(Board board,
                                    CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
        super(board);
        this.algo = new CaptureLineAlgo<>(board, algo);
    }

    @Override
    protected Collection<Calculated> calculate(ATTACKER attacker, KING king) {
        Collection<Calculated> lines = Stream.of(algo.calculate(attacker))
                .flatMap(Collection::stream)
                .filter(line -> line.contains(king.getPosition()))
                .collect(toList());

        return lines;
    }

    @Override
    protected Collection<PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>>
            createImpacts(ATTACKER attacker, KING king, Collection<Calculated> next) {

        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceCheckImpact<>(attacker, king, (Line) calculated))
                .collect(toList());

        return impacts;
    }
}