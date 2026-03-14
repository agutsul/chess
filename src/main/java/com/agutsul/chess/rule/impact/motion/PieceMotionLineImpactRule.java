package com.agutsul.chess.rule.impact.motion;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.MoveLineAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceMotionLineImpactRule<COLOR extends Color,
                                             PIECE extends Piece<COLOR> & Movable & Lineable>
        extends AbstractMotionImpactRule<COLOR,PIECE,PieceMotionImpact<COLOR,PIECE>> {

    private final Algo<PIECE,Collection<Position>> algo;

    public PieceMotionLineImpactRule(Board board,
                                     MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = new LinePositionAlgoAdapter<>(new MoveLineAlgoAdapter<>(board, algo));
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceMotionImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> positions) {

        var impacts = Stream.of(positions)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceMotionImpact<>(piece, (Position) calculated))
                .collect(toList());

        return impacts;
    }
}