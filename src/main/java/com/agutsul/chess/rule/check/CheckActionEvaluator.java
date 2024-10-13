package com.agutsul.chess.rule.check;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.Rule;

public interface CheckActionEvaluator<COLOR extends Color,
                                      KING extends KingPiece<COLOR>>
        extends Rule<KING, Collection<Action<?>>> {

}