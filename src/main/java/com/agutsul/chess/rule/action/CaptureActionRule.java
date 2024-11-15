package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CaptureActionRule<COLOR1 extends Color,
                                   COLOR2 extends Color,
                                   PIECE1 extends Piece<COLOR1> & Capturable,
                                   PIECE2 extends Piece<COLOR2>,
                                   ACTION extends PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
    extends Rule<PIECE1,Collection<ACTION>> {

}