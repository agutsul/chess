package com.agutsul.chess.rule.impact.domination;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.PieceDominationAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceDominationLineImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                 ATTACKED extends Piece<COLOR2>,
                                                 IMPACT extends PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractDominationImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final Algo<ATTACKER,Collection<Line>> algo;

    public PieceDominationLineImpactRule(Board board,
                                         CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next) {
        var attackedPositions = getAttackedPositions(piece.getColor());

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(line -> Stream.of(board.getPiece(line.getLast()))
                        .flatMap(Optional::stream)
                        .map(foundPiece -> {
                            var opponentActionPositions = Stream.of(board.getActions(foundPiece))
                                    .flatMap(Collection::stream)
                                    .map(Action::getPosition)
                                    .collect(toSet());

                            return attackedPositions.containsAll(opponentActionPositions)
                                    ? createAttackImpact(piece, foundPiece, line)
                                    : null;
                        })
                )
                .filter(Objects::nonNull)
                .map(impact -> new PieceDominationAttackImpact<>(impact))
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }
}