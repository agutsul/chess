package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Outpost_(chess)
abstract class AbstractOutpostImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable & Movable,
                                         IMPACT extends PieceOutpostImpact<COLOR,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements OutpostImpactRule<COLOR,PIECE,IMPACT> {

    AbstractOutpostImpactRule(Board board) {
        super(board, Impact.Type.OUTPOST);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculated> calculate(PIECE piece);

    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculated> next) {

        var opponentPawns = Stream.of(board.getPieces(piece.getColor().invert(), Piece.Type.PAWN))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(pawn -> pawn.getPosition().x(), identity()));

        var attackedPositions = Stream.of(opponentPawns.values())
                .flatMap(Collection::stream)
                .map(opponentPawn -> board.getImpacts(opponentPawn, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(PieceControlImpact::getTarget)
                .toList();

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                // confirm that position is not attacked by any opponent pawn
                .filter(position -> !attackedPositions.contains(position))
                // confirm that position can't be attacked by any opponent pawn in future
                .filter(position -> Stream.of(positionOf(position.x() - 1, position.y()), positionOf(position.x() - 1, position.y()))
                        .filter(Objects::nonNull)
                        .anyMatch(opponentPosition -> {
                            if (!opponentPawns.containsKey(opponentPosition.x())) {
                                return true;
                            }

                            var opponentPawn = opponentPawns.get(opponentPosition.x());
                            var visitedPositions = opponentPawn.getPositions();

                            return visitedPositions.contains(opponentPosition);
                        })
                )
                // confirm that position is under control by any player's pawn
                .filter(position -> Stream.of(board.getPieces(piece.getColor(), Piece.Type.PAWN))
                        .flatMap(Collection::stream)
                        .map(pawn -> board.getImpacts(pawn, Impact.Type.CONTROL))
                        .flatMap(Collection::stream)
                        .map(impact -> (PieceControlImpact<?,?>) impact)
                        .anyMatch(impact -> Objects.equals(impact.getTarget(), position))
                )
                .map(position -> new PieceOutpostImpact<>(piece, position))
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }
}