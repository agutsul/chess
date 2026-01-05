package com.agutsul.chess.rule.impact.sacrifice;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeAttackImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeMoveImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.SacrificeImpactRule;

// https://en.wikipedia.org/wiki/Sacrifice_(chess)
abstract class AbstractSacrificeImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                           ATTACKER extends Piece<COLOR2> & Capturable,
                                           ATTACKED extends Piece<COLOR2>,
                                           IMPACT extends PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
        extends AbstractRule<SACRIFICED,IMPACT,Impact.Type>
        implements SacrificeImpactRule<COLOR1,COLOR2,SACRIFICED,ATTACKER,IMPACT> {

    AbstractSacrificeImpactRule(Board board) {
        super(board, Impact.Type.SACRIFICE);
    }

    @Override
    public Collection<IMPACT> evaluate(SACRIFICED piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(SACRIFICED piece);

    protected Collection<IMPACT> createImpacts(SACRIFICED piece, Collection<Calculatable> next) {

       var opponentControls = getPieceControls(piece.getColor().invert());

       @SuppressWarnings("unchecked")
       var impacts = Stream.of(next)
               .flatMap(Collection::stream)
               .map(calculated -> (Position) calculated)
               .flatMap(position -> Stream.of(opponentControls.entrySet())
                       .flatMap(Collection::stream)
                       .filter(entry -> entry.getValue().contains(position))
                       .map(entry -> (ATTACKER) entry.getKey())
                       .map(attacker -> {

                           if (board.isEmpty(position)) {
                               return Optional.of(new PieceSacrificeMoveImpact<>(
                                       new PieceControlImpact<>(piece, position),
                                       createAttackImpact(attacker, piece, getAttackLine(attacker, piece))
                               ));
                           }

                           return Stream.of(board.getPiece(position))
                                   .flatMap(Optional::stream)
                                   .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                                   .map(opponentPiece -> new PieceSacrificeAttackImpact<>(
                                           createAttackImpact(piece, (ATTACKED) opponentPiece, getAttackLine(piece, opponentPiece)),
                                           createAttackImpact(attacker, piece, getAttackLine(attacker, piece))
                                   ))
                                   .map(impact -> (IMPACT) impact)
                                   .findFirst();
                       })
               )
               .flatMap(Optional::stream)
               .map(impact -> (IMPACT) impact)
               .collect(toList());

        return impacts;
    }

    protected Map<Piece<Color>,List<Position>> getPieceControls(Color color) {
        return Stream.of(board.getPieces(color))
                .flatMap(Collection::stream)
                .map(piece -> {
                    var controlPositions = Stream.of(board.getImpacts(piece, Impact.Type.CONTROL))
                            .flatMap(Collection::stream)
                            .map(impact -> (PieceControlImpact<?,?>) impact)
                            .map(PieceControlImpact::getPosition)
                            .distinct()
                            .collect(toList());

                    return Pair.of(piece, controlPositions);
                })
                .filter(pair -> !pair.getValue().isEmpty())
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

    protected Optional<Line> getAttackLine(Piece<?> piece1, Piece<?> piece2) {
        if (Objects.equals(piece1.getColor(), piece2.getColor())) {
            return Optional.empty();
        }

        return board.getLine(piece1, piece2);
    }
}