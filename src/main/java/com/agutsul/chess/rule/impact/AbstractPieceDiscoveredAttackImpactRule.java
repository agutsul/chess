package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

abstract class AbstractPieceDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       PIECE extends Piece<COLOR1>,
                                                       ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                       ATTACKED extends Piece<COLOR2>,
                                                       IMPACT extends PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
        extends AbstractDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,IMPACT>
        implements LineImpactRule {

    private final Algo<PIECE,Collection<Line>> algo;

    AbstractPieceDiscoveredAttackImpactRule(Board board, Algo<PIECE,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(PIECE piece) {
        // find all possible action positions
        var pieceActionPositions = Stream.of(board.getActions(piece))
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .collect(toSet());

        if (pieceActionPositions.isEmpty()) {
            return emptyList();
        }

        var lines = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                // check if there is piece action position outside line
                .filter(line  -> !line.containsAll(pieceActionPositions))
                // check if piece inside line
                .filter(line  -> line.contains(piece.getPosition()))
                .collect(toList());

        return lines;
    }
}