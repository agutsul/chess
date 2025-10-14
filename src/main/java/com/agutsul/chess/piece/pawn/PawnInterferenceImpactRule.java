package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.PieceInterferencePositionImpactRule;

final class PawnInterferenceImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PAWN extends PawnPiece<COLOR1>,
                                       PROTECTOR extends Piece<COLOR2> & Capturable,
                                       PROTECTED extends Piece<COLOR2>>
        extends PieceInterferencePositionImpactRule<COLOR1,COLOR2,PAWN,PROTECTOR,PROTECTED> {

    private final MovePieceAlgo<COLOR1,PAWN,Position> bigMoveAlgo;

    PawnInterferenceImpactRule(Board board,
                               MovePieceAlgo<COLOR1,PAWN,Position> moveAlgo,
                               MovePieceAlgo<COLOR1,PAWN,Position> bigMoveAlgo) {

        super(board, moveAlgo);
        this.bigMoveAlgo = bigMoveAlgo;
    }

    @Override
    protected Collection<Calculated> calculate(PAWN piece) {
        var positions = new ArrayList<Calculated>();
        positions.addAll(super.calculate(piece));
        positions.addAll(bigMoveAlgo.calculate(piece));
        return positions;
    }
}