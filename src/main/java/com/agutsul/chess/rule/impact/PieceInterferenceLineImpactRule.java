package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MoveLineAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;

public final class PieceInterferenceLineImpactRule<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   PIECE extends Piece<COLOR1> & Movable,
                                                   PROTECTOR extends Piece<COLOR2> & Capturable,
                                                   PROTECTED extends Piece<COLOR2>>
        extends AbstractInterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,
                                               PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>> {

    private final MovePieceAlgo<COLOR1,PIECE,Line> algo;

    public PieceInterferenceLineImpactRule(Board board,
                                           MovePieceAlgo<COLOR1,PIECE,Line> algo) {
        super(board);
        this.algo = new MoveLineAlgo<>(board, algo);
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        Collection<Calculated> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(toSet());

        return positions;
    }
}