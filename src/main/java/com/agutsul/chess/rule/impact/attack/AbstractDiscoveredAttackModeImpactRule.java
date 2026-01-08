package com.agutsul.chess.rule.impact.attack;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

abstract class AbstractDiscoveredAttackModeImpactRule<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      PIECE  extends Piece<COLOR1>,
                                                      ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                      ATTACKED extends Piece<COLOR2>,
                                                      IMPACT extends PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
        extends AbstractDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,IMPACT> {

    private final Algo<PIECE,Collection<Position>> algo;

    AbstractDiscoveredAttackModeImpactRule(Board board,
                                           Algo<PIECE,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        Collection<Calculatable> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> {
                    var optionalPiece = board.getPiece(position);
                    if (optionalPiece.isEmpty()) {
                        return true;
                    }

                    var foundPiece = optionalPiece.get();
                    return !Objects.equals(foundPiece.getColor(), piece.getColor());
                })
                .collect(toList());

        return positions;
    }
}