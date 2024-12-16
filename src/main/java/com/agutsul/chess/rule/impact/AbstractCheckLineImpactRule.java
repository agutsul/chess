package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

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
        var checkLines = new ArrayList<Calculated>();

        var lines = algo.calculate(attacker);
        for (var line : lines) {
            if (!line.contains(king.getPosition())) {
                continue;
            }

            var positions = filterCheckPositions(line, king);
            if (!positions.isEmpty()) {
                checkLines.add(new Line(positions));
            }
        }

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

    private List<Position> filterCheckPositions(Line line, KING king) {
        var positions = new ArrayList<Position>();
        for (var position : line) {
            var optionalPiece = board.getPiece(position);
            if (optionalPiece.isEmpty()) {
                positions.add(position);
                continue;
            }

            if (!Objects.equals(optionalPiece.get(), king)) {
                break;
            } else {
                positions.add(position);
                return positions;
            }
        }

        return emptyList();
    }
}