package com.agutsul.chess.rule.impact;

import static java.util.List.copyOf;

import java.util.Collection;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceSacrificePositionImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                              ATTACKER extends Piece<COLOR2> & Capturable,
                                              ATTACKED extends Piece<COLOR2>>
        extends AbstractSacrificeImpactRule<COLOR1,COLOR2,SACRIFICED,ATTACKER,ATTACKED,
                                            PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>> {

    private Algo<SACRIFICED,Collection<Position>> algo;

    public PieceSacrificePositionImpactRule(Board board,
                                            Algo<SACRIFICED,Collection<Position>> algo) {
        this(board);
        this.algo = algo;
    }

    protected PieceSacrificePositionImpactRule(Board board) {
        super(board);
    }

    @Override
    protected Collection<Calculated> calculate(SACRIFICED piece) {
        return copyOf(algo.calculate(piece));
    }
}