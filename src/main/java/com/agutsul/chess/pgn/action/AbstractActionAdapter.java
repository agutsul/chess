package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.remove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractActionAdapter
        implements ActionAdapter {

    final Board board;
    final Color color;

    AbstractActionAdapter(Board board, Color color) {
        this.board = board;
        this.color = color;
    }

    Optional<Piece<Color>> getMovablePiece(Piece.Type pieceType, String position) {
        return getMovablePiece(pieceType, null, position);
    }

    Optional<Piece<Color>> getMovablePiece(Piece.Type pieceType, String code, String position) {
        return getPiece(pieceType, code, position, Action.Type.MOVE);
    }

    Optional<Piece<Color>> getCapturablePiece(Piece.Type pieceType, String code, String position) {
        return getPiece(pieceType, code, position, Action.Type.CAPTURE);
    }

    Collection<Piece<Color>> findPieces(Piece.Type pieceType, String code) {
        var pieces = board.getPieces(color, pieceType).stream()
                .filter(piece -> code == null || contains(codeOf(piece.getPosition()), code))
                .toList();

        return pieces;
    }

    boolean containsAction(Piece<Color> piece, String position, Action.Type actionType) {
        var actions = board.getActions(piece, actionType);
        var actionExists = actions.stream()
                .map(Action::getPosition)
                .map(Position::codeOf)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return actionExists;
    }

    Optional<Piece<Color>> getPiece(Piece.Type pieceType, String code,
                                    String position, Action.Type actionType) {

        var foundPieces = findPieces(pieceType, code);
//        var pieces = foundPieces.stream()
//                .filter(piece -> containsAction(piece, position, actionType))
//                .toList();

        Collection<Piece<Color>> pieces = new ArrayList<>();
        for (var piece : foundPieces) {
            if (containsAction(piece, position, actionType)) {
                pieces.add(piece);
            }
        }

        if (pieces.isEmpty()) {
            return Optional.empty();
        }

        if (pieces.size() == 1) {
            return Optional.of(pieces.iterator().next());
        }

        var foundPiece = pieces.stream()
                .filter(piece -> {
                    if (Piece.Type.KING.equals(piece.getType())) {
                        return true;
                    }

                    var isPinned = ((Pinnable) piece).isPinned();
                    if (!isPinned) {
                        return true;
                    }

                    return isActionAvailable(piece, position);
                })
                .findFirst();

        return foundPiece;
    }

    boolean isActionAvailable(Piece<?> piece, String position) {
        // check if pinned piece action is inside checker's attack line
        var impacts = board.getImpacts(piece, Impact.Type.PIN);
        var checkImpact = impacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .map(PiecePinImpact::getTarget)
                .findFirst()
                .get();

        var checkLine = checkImpact.getAttackLine();
        if (checkLine.isPresent()) {
            var line = checkLine.get();
            if (line.contains(positionOf(position))) {
                return true;
            }
        }

        // check if pinned piece action is capturing checker piece
        var attacker = checkImpact.getSource();
        var pieceActions = piece.getActions(Action.Type.CAPTURE);

        var isCapturable = pieceActions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(AbstractCaptureAction::getTarget)
                .anyMatch(targetPiece -> Objects.equals(targetPiece, attacker));

        return isCapturable;
    }

    static final String adapt(Piece<Color> piece, String target) {
        var source = String.valueOf(piece.getPosition());
        return String.format("%s %s", source, target);
    }

    static String formatInvalidActionMessage(String action) {
        return String.format("Invalid action format: '%s'", action);
    }

    static String formatUnknownPieceMessage(String action) {
        return String.format("Unknown source piece for action: '%s'", action);
    }

    static String prepare(String action) {
        var command = remove(remove(action, "+"), "#");
        return remove(remove(command, "!"), "?");
    }
}