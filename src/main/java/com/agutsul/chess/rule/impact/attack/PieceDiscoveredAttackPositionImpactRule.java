package com.agutsul.chess.rule.impact.attack;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceDiscoveredAttackPositionImpactRule<COLOR1 extends Color,
                                                     COLOR2 extends Color,
                                                     PIECE  extends Piece<COLOR1>,
                                                     ATTACKER extends Piece<COLOR1> & Capturable,
                                                     ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                        PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>{

    public PieceDiscoveredAttackPositionImpactRule(Board board,
                                                   Algo<PIECE,Collection<Position>> algo) {
        super(board, algo);
    }
}