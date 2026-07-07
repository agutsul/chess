package com.agutsul.chess.rule.impact.outpost;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.Range;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractPieceImpactRule;
import com.agutsul.chess.rule.impact.OutpostImpactRule;

// https://en.wikipedia.org/wiki/Outpost_(chess)
abstract class AbstractOutpostImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable & Movable,
                                         IMPACT extends PieceOutpostImpact<COLOR,PIECE>>
        extends AbstractPieceImpactRule<COLOR,PIECE,IMPACT>
        implements OutpostImpactRule<COLOR,PIECE,IMPACT> {

    private final Algo<PIECE,Collection<Position>> algo;
    private final Range<Integer> lineRange;

    AbstractOutpostImpactRule(Board board, Range<Integer> lineRange,
                              Algo<PIECE,Collection<Position>> algo) {

        super(board, Impact.Type.OUTPOST);
        this.algo = algo;
        this.lineRange = lineRange;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculatable> next) {
        var opponentColor = piece.getColor().invert();

        var opponentPawnControlledPositions = getPawnControlledPositions(opponentColor);
        var playerPawnControlledPositions = getPawnControlledPositions(piece.getColor());

        if (opponentPawnControlledPositions.isEmpty()
                || playerPawnControlledPositions.isEmpty()) {

            return emptyList();
        }

        var opponentPawns = new ArrayListValuedHashMap<Integer,Piece<?>>();
        for (var opponentPawn : board.getPieces(opponentColor, Piece.Type.PAWN)) {
            opponentPawns.put(opponentPawn.getPosition().x(), opponentPawn);
        }

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::parallelStream)
                .map(calculated -> (Position) calculated)
                // confirm that position inside specified horizontal line range
                .filter(position -> lineRange.contains(position.y()))
                // confirm that position is not attacked by any opponent pawn
                .filter(position -> !opponentPawnControlledPositions.contains(position))
                // confirm that position can't be attacked by any opponent pawn in future
                .filter(position -> Stream.of(position.x() + 1, position.x() - 1)
                        .map(x -> board.getPosition(x, position.y()))
                        .flatMap(Optional::stream)
                        .anyMatch(opponentPosition -> {
                            if (!opponentPawns.containsKey(opponentPosition.x())) {
                                return true;
                            }

                            var isVisited = Stream.of(opponentPawns.get(opponentPosition.x()))
                                    .flatMap(Collection::parallelStream)
                                    .map(Piece::getPositions)
                                    .allMatch(visitedPositions -> visitedPositions.contains(opponentPosition));

                            return isVisited;
                        })
                )
                // confirm that position is under control by any player's pawn
                .filter(position -> playerPawnControlledPositions.contains(position))
                .map(position -> new PieceOutpostImpact<>(piece, position))
                .map(impact -> (IMPACT) impact)
                .toList();

        return impacts;
    }

    private Collection<Position> getPawnControlledPositions(Color color) {
        return Stream.of(board.getPieces(color, Piece.Type.PAWN))
                .flatMap(Collection::parallelStream)
                .map(pawn -> board.getImpacts(pawn, Impact.Type.CONTROL))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(PieceControlImpact::getTarget)
                .collect(toSet());
    }
}