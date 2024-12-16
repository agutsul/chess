package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.remove;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PiecePinImpact;
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
        var foundPiece = foundPieces.stream()
                .filter(piece -> containsAction(piece, position, actionType))
                .filter(piece -> {
                    if (Piece.Type.KING.equals(piece.getType())) {
                        return true;
                    }

                    var isPinned = ((Pinnable) piece).isPinned();
                    if (!isPinned) {
                        return true;
                    }

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
                })
                .findFirst();

        return foundPiece;
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