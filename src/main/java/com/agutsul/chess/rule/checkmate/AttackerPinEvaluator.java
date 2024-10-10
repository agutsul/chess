package com.agutsul.chess.rule.checkmate;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

final class AttackerPinEvaluator<COLOR extends Color,
                                 KING extends KingPiece<COLOR>>
        implements CheckMateEvaluator<COLOR, KING> {

    private static final Logger LOGGER = getLogger(AttackerPinEvaluator.class);

    private final Board board;

    AttackerPinEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KING king) {
        LOGGER.info("Evaluate attacker block for king '{}'", king);
        // get all piece moves of the same color as king except the king itself
        var pieceMovePositions = board.getPieces(king.getColor()).stream()
                .filter(piece -> !Piece.Type.KING.equals(piece.getType()))
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::stream)
                .map(action -> {
                    if (Action.Type.MOVE.equals(action.getType())) {
                        return (PieceMoveAction<?,?>) action;
                    }

                    if (Action.Type.PROMOTE.equals(action.getType())) {
                        var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

                        if (Action.Type.MOVE.equals(sourceAction.getType())) {
                            return (PieceMoveAction<?,?>) sourceAction;
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .map(PieceMoveAction::getPosition)
                .collect(toSet());

        if (pieceMovePositions.isEmpty()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        var attackers = board.getAttackers((Piece<Color>) king);
        var isPinnable = attackers.stream()
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::stream)
                .map(action -> {
                    if (Action.Type.CAPTURE.equals(action.getType())
                            || Action.Type.EN_PASSANT.equals(action.getType())) {

                        return (PieceCaptureAction<?,?,?,?>) action;
                    }

                    if (Action.Type.PROMOTE.equals(action.getType())) {
                        var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

                        if (Action.Type.CAPTURE.equals(sourceAction.getType())) {
                            return (PieceCaptureAction<?,?,?,?>) sourceAction;
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .filter(action -> Objects.equals(action.getTarget(), king))
                .map(PieceCaptureAction::getAttackLine)
                .flatMap(Line::stream)
                .anyMatch(position -> pieceMovePositions.contains(position));

        return isPinnable;
    }
}