package com.agutsul.chess.rule.impact.attack.impending;

import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceImpendingAttackPositionImpactRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                    ATTACKED extends Piece<COLOR2>,
                                                    IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractImpendingAttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final Algo<ATTACKER,Collection<Position>> algo;

    public PieceImpendingAttackPositionImpactRule(Board board,
                                                  Algo<ATTACKER,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER attacker) {
        return unmodifiableCollection(algo.calculate(attacker));
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::parallelStream)
                .collect(toMap(Piece::getPosition, identity()));

        var opponentPositions = opponentPieces.keySet();

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::parallelStream)
                .map(calculated -> (Position) calculated)
                .flatMap(position -> Stream.of(piece.getNext(position))
                        .flatMap(Collection::parallelStream)
                        .filter(nextCalculated -> nextCalculated instanceof Position)
                        .map(nextCalculated -> (Position) nextCalculated)
                        .filter(nextPosition -> !Objects.equals(nextPosition, piece.getPosition()))
                        .filter(nextPosition -> opponentPositions.contains(nextPosition))
                        .map(nextPosition -> opponentPieces.get(nextPosition))
                        .map(opponentPiece -> createImpact(piece, position, (ATTACKED) opponentPiece))
                )
                .filter(Objects::nonNull)
                .toList();

        return impacts;
    }
}