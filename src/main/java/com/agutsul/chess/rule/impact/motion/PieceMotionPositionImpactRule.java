package com.agutsul.chess.rule.impact.motion;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceMotionPositionImpactRule<COLOR extends Color,
                                           PIECE extends Piece<COLOR> & Movable>
        extends AbstractMotionImpactRule<COLOR,PIECE,PieceMotionImpact<COLOR,PIECE>> {

    public PieceMotionPositionImpactRule(Board board,
                                         MovePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<PieceMotionImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> positions) {

        var impacts = Stream.of(positions)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceMotionImpact<>(piece, (Position) calculated))
                .toList();

        return impacts;
    }
}