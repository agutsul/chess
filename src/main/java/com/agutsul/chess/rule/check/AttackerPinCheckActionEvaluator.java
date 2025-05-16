package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class AttackerPinCheckActionEvaluator
        extends AbstractMoveCheckActionEvaluator {

    AttackerPinCheckActionEvaluator(Board board,
                                    Collection<Action<?>> pieceActions) {
        super(board, pieceActions);
    }

    @Override
    Collection<Action<?>> process(KingPiece<?> king,
                                  Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                  Collection<Action<?>> actions) {

        Collection<Action<?>> pinActions = checkActions.stream()
                .map(PieceCaptureAction::getLine)
                .flatMap(Optional::stream)
                .flatMap(line -> actions.stream().filter(action -> line.contains(action.getPosition())))
                .collect(toSet());

        return pinActions;
    }
}