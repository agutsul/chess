package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.block.PieceBlockPositionImpactRule;

final class PawnBlockImpactRule<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PAWN extends PawnPiece<COLOR1>,
                                ATTACKED extends Piece<COLOR1>,
                                ATTACKER extends Piece<COLOR2> & Capturable>
        extends PieceBlockPositionImpactRule<COLOR1,COLOR2,PAWN,ATTACKED,ATTACKER> {

    private final MovePieceAlgo<COLOR1,PAWN,Position> bigMoveAlgo;

    PawnBlockImpactRule(Board board,
                        MovePieceAlgo<COLOR1,PAWN,Position> moveAlgo,
                        MovePieceAlgo<COLOR1,PAWN,Position> bigMoveAlgo) {

        super(board, moveAlgo);
        this.bigMoveAlgo = bigMoveAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var positions = new ArrayList<Calculatable>();
        positions.addAll(super.calculate(piece));
        positions.addAll(bigMoveAlgo.calculate(piece));
        return positions;
    }
}