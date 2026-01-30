package com.agutsul.chess.rule.impact.outpost;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.OutpostImpactRule;

// https://en.wikipedia.org/wiki/Outpost_(chess)
abstract class AbstractOutpostImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable & Movable,
                                         IMPACT extends PieceOutpostImpact<COLOR,PIECE>>
        extends AbstractImpactRule<COLOR,PIECE,IMPACT>
        implements OutpostImpactRule<COLOR,PIECE,IMPACT> {

    AbstractOutpostImpactRule(Board board) {
        super(board, Impact.Type.OUTPOST);
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculatable> next) {
        var opponentPawns = board.getPieces(piece.getColor().invert(), Piece.Type.PAWN);

        var opponentPawnsCache = new ArrayListValuedHashMap<Integer,Piece<Color>>();
        Stream.of(opponentPawns)
                .flatMap(Collection::stream)
                .forEach(pawn -> opponentPawnsCache.put(pawn.getPosition().x(), pawn));

        var attackedPositions = Stream.of(opponentPawns)
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
                .filter(position -> Stream.of(position.x() + 1, position.x() - 1)
                        .map(x -> positionOf(x, position.y()))
                        .filter(Objects::nonNull)
                        .anyMatch(opponentPosition -> {
                            if (!opponentPawnsCache.containsKey(opponentPosition.x())) {
                                return true;
                            }

                            var isVisited = Stream.of(opponentPawnsCache.get(opponentPosition.x()))
                                    .flatMap(Collection::stream)
                                    .map(Piece::getPositions)
                                    .allMatch(visitedPositions -> visitedPositions.contains(opponentPosition));

                            return isVisited;
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