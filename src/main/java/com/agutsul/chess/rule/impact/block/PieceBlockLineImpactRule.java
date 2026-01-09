package com.agutsul.chess.rule.impact.block;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.MoveLineAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceBlockLineImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            BLOCKER extends Piece<COLOR1> & Movable & Lineable,
                                            ATTACKED extends Piece<COLOR1>,
                                            ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends AbstractBlockImpactRule<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER,
                                        PieceBlockImpact<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER>> {

    private final Algo<BLOCKER,Collection<Position>> algo;

    public PieceBlockLineImpactRule(Board board,
                                    MovePieceAlgo<COLOR1,BLOCKER,Line> algo) {
        super(board);
        this.algo = new LinePositionAlgoAdapter<>(new MoveLineAlgoAdapter<>(board, algo));
    }

    @Override
    protected Collection<Calculatable> calculate(BLOCKER piece) {
        Collection<Calculatable> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .distinct()
                .collect(toList());

        return positions;
    }
}