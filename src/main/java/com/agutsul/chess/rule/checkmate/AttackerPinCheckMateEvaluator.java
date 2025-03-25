package com.agutsul.chess.rule.checkmate;

import static com.agutsul.chess.piece.Piece.isPawn;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

final class AttackerPinCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(AttackerPinCheckMateEvaluator.class);

    private final Board board;

    AttackerPinCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        LOGGER.info("Evaluate attacker block for king '{}'", king);
        // get all piece moves of the same color as king except the king itself
        var pieceMovePositions = board.getPieces(king.getColor()).stream()
                .filter(not(Piece::isKing))
                // confirm that piece not already pinned
                .filter(piece -> !((Pinnable) piece).isPinned())
                // find all possible move actions
                .map(piece -> {
                    var actions = new ArrayList<Action<?>>(board.getActions(piece, Action.Type.MOVE));
                    if (isPawn(piece)) {
                        actions.addAll(board.getActions(piece, Action.Type.BIG_MOVE));
                    }

                    return actions;
                })
                .flatMap(Collection::stream)
                .map(action -> (PieceMoveAction<?,?>) action)
                .map(PieceMoveAction::getPosition)
                .collect(toSet());

        if (pieceMovePositions.isEmpty()) {
            return false;
        }

        var attackers = board.getAttackers(king);
        var isPinnable = attackers.stream()
                .map(piece -> board.getActions(piece, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .filter(Action::isCapture)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), king))
                .map(PieceCaptureAction::getLine)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(Line::stream)
                .anyMatch(position -> pieceMovePositions.contains(position));

        return isPinnable;
    }
}