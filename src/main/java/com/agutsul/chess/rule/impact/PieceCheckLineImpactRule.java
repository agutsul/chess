package com.agutsul.chess.rule.impact;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
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
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PieceCheckLineImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends Piece<COLOR1> & Capturable,
                                      KING extends KingPiece<COLOR2>>
        extends AbstractCheckImpactRule<COLOR1,COLOR2,ATTACKER,KING,
                                        PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>> {

    private final CapturePieceAlgo<COLOR1,ATTACKER,Line> algo;

    public PieceCheckLineImpactRule(Board board,
                                    CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(ATTACKER attacker, KING king) {
        var lines = algo.calculate(attacker);

        Collection<Calculated> checkLines = lines.stream()
                .filter(line -> line.contains(king.getPosition()))
                .map(line -> findCheckPositions(line, king))
                .filter(not(Collection::isEmpty))
                .map(Line::new)
                .collect(toList());

        return checkLines;
    }

    @Override
    protected Collection<PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>>
            createImpacts(ATTACKER attacker, KING king, Collection<Calculated> lines) {

        var impacts = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .map(line -> new PieceCheckImpact<>(attacker, king, line))
                .toList();

        return impacts;
    }

    private List<Position> findCheckPositions(Line line, KING king) {
        var positions = new ArrayList<Position>();
        for (var position : line) {
            var optionalPiece = board.getPiece(position);
            if (optionalPiece.isPresent()) {
                if (Objects.equals(optionalPiece.get(), king)) {
                    positions.add(position);
                } else {
                    positions.clear();
                }

                break;
            }

            positions.add(position);
        }

        return positions;
    }
}