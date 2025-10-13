package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.PieceBlockPositionImpactRule;

final class PawnBlockImpactRule<COLOR1 extends Color,
                                COLOR2 extends Color,
                                BLOCKER extends Piece<COLOR1> & Movable,
                                ATTACKED extends Piece<COLOR1>,
                                ATTACKER extends Piece<COLOR2> & Capturable>
        extends PieceBlockPositionImpactRule<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER> {

    private final MovePieceAlgo<COLOR1,BLOCKER,Position> bigMoveAlgo;

    PawnBlockImpactRule(Board board,
                        MovePieceAlgo<COLOR1,BLOCKER,Position> moveAlgo,
                        MovePieceAlgo<COLOR1,BLOCKER,Position> bigMoveAlgo) {

        super(board, moveAlgo);
        this.bigMoveAlgo = bigMoveAlgo;
    }

    @Override
    protected Collection<Calculated> calculate(BLOCKER piece) {
        var positions = new ArrayList<Calculated>();
        positions.addAll(super.calculate(piece));
        positions.addAll(bigMoveAlgo.calculate(piece));
        return positions;
    }
}
