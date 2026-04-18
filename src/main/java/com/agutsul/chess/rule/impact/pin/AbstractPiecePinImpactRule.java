package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.Rule;

// https://en.wikipedia.org/wiki/Pin_(chess)
abstract class AbstractPiecePinImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable,
                                          DEFENDED extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                          IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    @SuppressWarnings("unchecked")
    AbstractPiecePinImpactRule(Board board) {
        super(board);
        this.rule = new CompositeRule<>(
                new PieceAbsolutePinImpactRule<>(board),
                new PieceRelativePinImpactRule<>(board)
        );
    }

    @Override
    protected Collection<IMPACT> createImpacts(PINNED piece,
                                               Collection<Calculatable> next) {

        var positions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> {
                    var optionalPiece = board.getPiece(position);
                    if (optionalPiece.isEmpty()) {
                        return true;
                    }

                    var foundPiece = optionalPiece.get();
                    return !Objects.equals(foundPiece.getColor(), piece.getColor());
                })
                .toList();

        if (positions.isEmpty()) {
            return emptyList();
        }

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
                .toList();

        return impacts;
    }
}