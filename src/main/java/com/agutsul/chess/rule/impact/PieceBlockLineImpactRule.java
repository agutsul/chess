package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MoveLineAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;

public final class PieceBlockLineImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            BLOCKER extends Piece<COLOR1> & Movable,
                                            ATTACKED extends Piece<COLOR1>,
                                            ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractBlockImpactRule<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER,
                                        PieceBlockImpact<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER>> {

    private final MovePieceAlgo<COLOR1,BLOCKER,Line> algo;

    public PieceBlockLineImpactRule(Board board,
                                    MovePieceAlgo<COLOR1,BLOCKER,Line> algo) {
        super(board);
        this.algo = new MoveLineAlgo<>(board, algo);
    }

    @Override
    protected Collection<Calculated> calculate(BLOCKER piece) {
        Collection<Calculated> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(toSet());

        return positions;
    }
}