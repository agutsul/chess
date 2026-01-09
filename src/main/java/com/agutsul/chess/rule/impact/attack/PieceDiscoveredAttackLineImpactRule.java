package com.agutsul.chess.rule.impact.attack;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;

public class PieceDiscoveredAttackLineImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 PIECE  extends Piece<COLOR1> & Lineable,
                                                 ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                 ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                        PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>> {

    public PieceDiscoveredAttackLineImpactRule(Board board,
                                               Algo<PIECE,Collection<Line>> algo) {

        super(board, new LinePositionAlgoAdapter<>(algo));
    }
}