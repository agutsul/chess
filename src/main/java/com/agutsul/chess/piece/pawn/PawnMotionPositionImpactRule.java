package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.motion.PieceMotionPositionImpactRule;

final class PawnMotionPositionImpactRule<COLOR extends Color,
                                         PAWN extends PawnPiece<COLOR>>
        extends PieceMotionPositionImpactRule<COLOR,PAWN> {

    private final MovePieceAlgo<COLOR,PAWN,Position> bigMoveAlgo;

    PawnMotionPositionImpactRule(Board board,
                                 MovePieceAlgo<COLOR,PAWN,Position> moveAlgo,
                                 MovePieceAlgo<COLOR,PAWN,Position> bigMoveAlgo) {

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