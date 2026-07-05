package com.agutsul.chess.rule.impact.interference;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.MoveLineAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;

public final class PieceInterferenceLineImpactRule<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   PIECE extends Piece<COLOR1> & Movable & Lineable,
                                                   PROTECTOR extends Piece<COLOR2> & Capturable & Lineable,
                                                   PROTECTED extends Piece<COLOR2>>
        extends AbstractInterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,
                                               PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>> {

    public PieceInterferenceLineImpactRule(Board board,
                                           MovePieceAlgo<COLOR1,PIECE,Line> algo) {

        super(board, new LinePositionAlgoAdapter<>(
                new MoveLineAlgoAdapter<>(board, algo)
        ));
    }
}