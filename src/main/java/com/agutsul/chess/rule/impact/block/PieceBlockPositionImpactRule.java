package com.agutsul.chess.rule.impact.block;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceBlockPositionImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          BLOCKER extends Piece<COLOR1> & Movable,
                                          ATTACKED extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractBlockImpactRule<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER,
                                        PieceBlockImpact<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER>> {

    private final MovePieceAlgo<COLOR1,BLOCKER,Position> algo;

    public PieceBlockPositionImpactRule(Board board,
                                        MovePieceAlgo<COLOR1,BLOCKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(BLOCKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}