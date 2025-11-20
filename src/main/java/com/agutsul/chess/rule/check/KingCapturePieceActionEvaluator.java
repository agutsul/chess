package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

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
        var filteredActions = Stream.of(pieceActions)
                .flatMap(Collection::stream)
                .filter(Action::isCapture)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .toList();

        var opponentColor = king.getColor().invert();

        Collection<Action<?>> actions = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(piece -> !((Protectable) piece).isProtected())
                .filter(piece -> !board.isMonitored(piece.getPosition(), opponentColor))
                .flatMap(piece -> filteredActions.stream()
                        .filter(action -> Objects.equals(action.getTarget(), piece))
                )
                .collect(toSet());

        return actions;
    }
}