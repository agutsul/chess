package com.agutsul.chess.rule.checkmate;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.Rule;

public interface CheckMateEvaluator<COLOR extends Color,
                                    KING extends KingPiece<COLOR>>
        extends Rule<KING, Boolean> {

}