package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

public abstract class AbstractCheckLineImpactRule<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  ATTACKER extends Piece<COLOR1> & Capturable,
                                                  KING extends KingPiece<COLOR2>,
                                                  IMPACT extends PieceCheckImpact<COLOR1,COLOR2,ATTACKER,KING>>
        extends AbstractCheckImpactRule<COLOR1,COLOR2,ATTACKER,KING,IMPACT> {

    protected final CapturePieceAlgo<COLOR1,ATTACKER,Line> algo;

    protected AbstractCheckLineImpactRule(Board board,
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
                .filter(positions -> !positions.isEmpty())
                .map(Line::new)
                .collect(toList());

        return checkLines;
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER attacker, KING king,
                                               Collection<Calculated> calculatedLines) {

        var impacts = calculatedLines.stream()
                .map(line -> createImpact(attacker, king, (Line) line))
                .toList();

        return impacts;
    }

    protected abstract IMPACT createImpact(ATTACKER attacker, KING king, Line line);

    private List<Position> findCheckPositions(Line line, KING king) {
        var positions = new ArrayList<Position>();
        for (var position : line) {
            var optionalPiece = board.getPiece(position);
            if (optionalPiece.isEmpty()) {
                positions.add(position);
                continue;
            }

            if (Objects.equals(optionalPiece.get(), king)) {
                positions.add(position);
                break;
            } else {
                positions.clear();
                break;
            }
        }

        return positions;
    }
}