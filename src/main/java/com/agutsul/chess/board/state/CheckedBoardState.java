package com.agutsul.chess.board.state;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class CheckedBoardState
        extends AbstractBoardState {

    public CheckedBoardState(Board board, Color checkedColor) {
        super(BoardState.Type.CHECKED, board, checkedColor);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        var allPieceActions = piece.getActions();
        if (!Objects.equals(piece.getColor(), color)) {
            return allPieceActions;
        }

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return allPieceActions;
        }

        var checkedKing = optionalKing.get();

        var attackers = board.getAttackers(checkedKing);
        var checkActions = getCheckActions(attackers, checkedKing);

        var filteredActions = new ArrayList<Action<?>>();
        // check if piece actions contains some to capture attacker
        filteredActions.addAll(captureAttackerActions(attackers, allPieceActions));

        if (Objects.equals(checkedKing, piece)) {
            // check king's ability to escape
            filteredActions.addAll(moveKingActions(checkActions, allPieceActions));
        } else {
            // check if piece actions contains ability to block attacker piece
            filteredActions.addAll(blockAttackerActions(checkActions, allPieceActions));
        }

        return filteredActions;
    }

    private Collection<PieceCaptureAction<?,?,?,?>> getCheckActions(Collection<Piece<Color>> attackers,
                                                                    KingPiece<Color> king) {
        return attackers.stream()
                .map(attacker -> board.getActions(attacker))
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
                .filter(action -> Objects.equals(king, action.getTarget()))
                .filter(action -> !action.getAttackLine().isEmpty())
                .collect(toList());
    }

    private Collection<Action<?>> captureAttackerActions(Collection<Piece<Color>> attackers,
                                                         Collection<Action<?>> pieceActions) {
        var actions = new ArrayList<Action<?>>();
        for (var attacker : attackers) {
            for (var pieceAction : pieceActions) {
                if (Action.Type.CAPTURE.equals(pieceAction.getType())) {
                    var capturedPiece = ((PieceCaptureAction<?,?,?,?>) pieceAction).getTarget();
                    if (Objects.equals(capturedPiece, attacker)) {
                        actions.add(pieceAction);
                    }
                }

                if (Action.Type.PROMOTE.equals(pieceAction.getType())) {
                    var sourceAction = ((PiecePromoteAction<?,?>) pieceAction).getSource();
                    if (Action.Type.CAPTURE.equals(sourceAction.getType())) {
                        var capturedPiece = ((PieceCaptureAction<?,?,?,?>) sourceAction).getTarget();
                        if (Objects.equals(capturedPiece, attacker)) {
                            actions.add(pieceAction);
                        }
                    }
                }
            }
        }

        return actions;
    }

    private Collection<Action<?>> blockAttackerActions(Collection<PieceCaptureAction<?,?,?,?>> attackerActions,
                                                       Collection<Action<?>> pieceActions) {
        var actions = new ArrayList<Action<?>>();
        for (var checkedAction : attackerActions) {
            var attackLine = checkedAction.getAttackLine();

            for (var pieceAction : pieceActions) {
                if (Action.Type.MOVE.equals(pieceAction.getType())) {
                    var moveAction = (PieceMoveAction<?,?>) pieceAction;
                    if (attackLine.contains(moveAction.getPosition())) {
                        actions.add(pieceAction);
                    }
                }

                if (Action.Type.PROMOTE.equals(pieceAction.getType())) {
                    var sourceAction = ((PiecePromoteAction<?,?>) pieceAction).getSource();
                    if (Action.Type.MOVE.equals(sourceAction.getType())) {
                        var moveAction = (PieceMoveAction<?,?>) sourceAction;
                        if (attackLine.contains(moveAction.getPosition())) {
                            actions.add(pieceAction);
                        }
                    }
                }
            }
        }
        return actions;
    }

    private Collection<Action<?>> moveKingActions(Collection<PieceCaptureAction<?,?,?,?>> attackerActions,
                                                    Collection<Action<?>> pieceActions) {
        var actions = new ArrayList<Action<?>>();
        for (var checkedAction : attackerActions) {
            var attackLine = checkedAction.getAttackLine();

            for (var pieceAction : pieceActions) {
                if (Action.Type.MOVE.equals(pieceAction.getType())) {
                    var moveAction = (PieceMoveAction<?,?>) pieceAction;

                    var targetPosition = moveAction.getPosition();
                    @SuppressWarnings("unchecked")
                    var sourcePiece = (Piece<Color>) moveAction.getSource();
                    var attackerColor = sourcePiece.getColor().invert();

                    if (!attackLine.contains(targetPosition)
                            && !board.isAttacked(targetPosition, attackerColor)
                            && !board.isMonitored(targetPosition, attackerColor)) {

                        actions.add(pieceAction);
                    }
                }
            }
        }
        return actions;
    }
}