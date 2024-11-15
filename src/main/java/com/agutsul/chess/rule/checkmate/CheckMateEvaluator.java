package com.agutsul.chess.rule.checkmate;

import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.Rule;

public interface CheckMateEvaluator
        extends Rule<KingPiece<?>,Boolean> {

}