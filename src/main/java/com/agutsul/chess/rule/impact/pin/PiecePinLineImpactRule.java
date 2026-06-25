package com.agutsul.chess.rule.impact.pin;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;

public class PiecePinLineImpactRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable & Lineable,
                                    DEFENDED extends Piece<COLOR1>,
                                    ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                    IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>>
        extends AbstractPiecePinImpactRule<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,IMPACT> {

    public PiecePinLineImpactRule(Board board, Algo<PINNED,Collection<Line>> algo) {
        super(board, new LinePositionAlgoAdapter<>(algo));
    }
}