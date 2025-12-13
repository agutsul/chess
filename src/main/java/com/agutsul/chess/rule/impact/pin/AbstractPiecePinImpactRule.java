package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePartialPinImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;

// https://en.wikipedia.org/wiki/Pin_(chess)
abstract class AbstractPiecePinImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          PINNED extends Piece<COLOR1> & Pinnable,
                                          PIECE  extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable,
                                          IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    @SuppressWarnings("unchecked")
    AbstractPiecePinImpactRule(Board board) {
        super(board);
        this.rule = new CompositePieceRule<>(
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
                .collect(toList());

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
                .collect(toList());

        return impacts;
    }
}