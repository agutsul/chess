package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class KingCapturePieceActionEvaluator
        implements CheckActionEvaluator {

    private final Board board;
    private final Collection<Action<?>> pieceActions;

    KingCapturePieceActionEvaluator(Board board,
                                    Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var filteredActions = this.pieceActions.stream()
                .filter(Action::isCapture)
                .toList();

        var opponentPieces = board.getPieces(king.getColor().invert());
        Collection<Action<?>> actions = opponentPieces.stream()
                .filter(piece -> !((Protectable) piece).isProtected())
                .flatMap(piece -> filteredActions.stream()
                        .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                        .filter(action -> Objects.equals(action.getTarget(), piece)))
                .collect(toSet());

        return actions;
    }
}