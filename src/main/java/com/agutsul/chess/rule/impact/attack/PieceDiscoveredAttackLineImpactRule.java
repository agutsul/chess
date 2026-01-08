package com.agutsul.chess.rule.impact.attack;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceDiscoveredAttackLineImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 PIECE  extends Piece<COLOR1> & Lineable,
                                                 ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                 ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                        PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>> {

    public PieceDiscoveredAttackLineImpactRule(Board board,
                                               Algo<PIECE,Collection<Line>> algo) {

        super(board, new AlgoAdapter<>(algo));
    }

    private static final class AlgoAdapter<COLOR extends Color,PIECE extends Piece<COLOR>>
            implements Algo<PIECE,Collection<Position>> {

        private final Algo<PIECE,Collection<Line>> algo;

        AlgoAdapter(Algo<PIECE,Collection<Line>> algo) {
            this.algo = algo;
        }

        @Override
        public Collection<Position> calculate(PIECE piece) {
            return Stream.of(algo.calculate(piece))
                    .flatMap(Collection::stream)
                    .flatMap(Collection::stream)
                    .collect(toList());
        }
    }
}