package com.agutsul.chess.rule.impact.interference;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceInterferencePositionImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 PIECE extends Piece<COLOR1> & Movable,
                                                 PROTECTOR extends Piece<COLOR2> & Capturable & Lineable,
                                                 PROTECTED extends Piece<COLOR2>>
        extends AbstractInterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,
                                               PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>> {

    private final Algo<PIECE,Collection<Position>> algo;

    public PieceInterferencePositionImpactRule(Board board,
                                               MovePieceAlgo<COLOR1,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}