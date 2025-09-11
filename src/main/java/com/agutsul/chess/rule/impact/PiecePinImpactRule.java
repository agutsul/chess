package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.PinPieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;

// https://en.wikipedia.org/wiki/Pin_(chess)
public class PiecePinImpactRule<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PINNED extends Piece<COLOR1> & Pinnable,
                                PIECE  extends Piece<COLOR1>,
                                ATTACKER extends Piece<COLOR2> & Capturable,
                                IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractRule<PINNED,IMPACT,Impact.Type>
        implements PinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    public PiecePinImpactRule(Board board) {
        this(board, new PinPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private PiecePinImpactRule(Board board, Algo<PINNED,Collection<Line>> algo) {
        super(board, Impact.Type.PIN);
        this.rule = new CompositePieceRule<>(
                new PieceAbsolutePinImpactRule<>(board, algo),
                new PieceRelativePinImpactRule<>(board, algo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(PINNED piece) {
        var positions = Stream.of(piece.getActions())
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .collect(toSet());

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(rule.evaluate(piece))
                .flatMap(Collection::stream)
                .map(impact -> {
                    var line = impact.getLine();
                    return line.containsAny(positions)
                            ? new PiecePartialPinImpact<>(impact)
                            : impact;
                })
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }
}