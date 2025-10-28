package com.agutsul.chess.rule.impact;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceInterferencePositionImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 PIECE extends Piece<COLOR1> & Movable,
                                                 PROTECTOR extends Piece<COLOR2> & Capturable,
                                                 PROTECTED extends Piece<COLOR2>>
        extends AbstractInterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,
                                               PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>> {

    private final MovePieceAlgo<COLOR1,PIECE,Position> algo;

    public PieceInterferencePositionImpactRule(Board board,
                                               MovePieceAlgo<COLOR1,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        return List.copyOf(algo.calculate(piece));
    }
}