package com.agutsul.chess.rule.check;

import java.util.Collection;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.Rule;

public interface CheckActionEvaluator
        extends Rule<KingPiece<?>,Collection<Action<?>>> {

}