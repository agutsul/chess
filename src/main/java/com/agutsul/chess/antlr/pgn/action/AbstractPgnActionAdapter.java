package com.agutsul.chess.antlr.pgn.action;

import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.BAD_MOVE_CODE;
import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.CHECKMATE_CODE;
import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.CHECK_CODE;
import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.GOOD_MOVE_CODE;
import static com.agutsul.chess.piece.Piece.isKing;
import static com.agutsul.chess.position.Position.codeOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.remove;

import java.util.Collection;
import java.util.HashMap;
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

abstract class AbstractPgnActionAdapter
        implements PgnActionAdapter {

    final Board board;
    final Color color;

    AbstractPgnActionAdapter(Board board, Color color) {
        this.board = board;
        this.color = color;
    }

    Optional<Piece<Color>> getMovablePiece(Piece.Type pieceType, String position) {
        var piece = getPiece(pieceType, null, position, Action.Type.MOVE);
        if (piece.isPresent()) {
            return piece;
        }

        return getPiece(pieceType, null, position, Action.Type.BIG_MOVE);
    }

    Optional<Piece<Color>> getMovablePiece(Piece.Type pieceType, String code, String position) {
        return getPiece(pieceType, code, position, Action.Type.MOVE);
    }

    Optional<Piece<Color>> getCapturablePiece(Piece.Type pieceType, String code, String position) {
        return getPiece(pieceType, code, position, Action.Type.CAPTURE);
    }

    Optional<Piece<Color>> getPromotablePiece(String code, String position) {
        return getPiece(Piece.Type.PAWN, code, position, Action.Type.PROMOTE);
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

        var map = new HashMap<Piece<Color>,Boolean>();

        var foundPieces = findPieces(pieceType, code);
        for (var piece : foundPieces) {
            if (!containsAction(piece, position, actionType)) {
                continue;
            }

            if (isKing(piece)) {
                var isAttacked = board.isAttacked(
                        positionOf(position),
                        piece.getColor().invert()
                );

                if (isAttacked) {
                    continue;
                }

                return Optional.of(piece);
            }

            map.put(piece, ((Pinnable) piece).isPinned());
        }

        var unPinnedPiece = map.entrySet().stream()
            .filter(entry -> !entry.getValue()) // filter unpinned
            .map(entry -> entry.getKey())
            .findFirst();

        if (unPinnedPiece.isPresent()) {
            return unPinnedPiece;
        }

        // check actions for pinned pieces and find piece with available capture action
        var piece = map.entrySet().stream()
            .filter(entry -> entry.getValue()) // filter pinned
            .map(entry -> entry.getKey())
            .filter(pinnedPiece -> isActionAvailable(pinnedPiece, position))
            .findFirst();

        return piece;
    }

    boolean isActionAvailable(Piece<?> piece, String position) {
        // check if pinned piece action is inside checker's attack line
        var impacts = board.getImpacts(piece, Impact.Type.PIN);
        var checkImpact = impacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .map(PiecePinImpact::getTarget)
                .findFirst()
                .get();

        var checkLine = checkImpact.getLine();
        if (checkLine.isPresent()) {
            var line = checkLine.get();
            return line.contains(positionOf(position));
        }

        // check if pinned piece action is capturing checker piece
        var attacker = checkImpact.getSource();
        var pieceActions = piece.getActions(Action.Type.CAPTURE);

        // TODO: confirm that targetPiece is related to provided position
        var isCapturable = pieceActions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(AbstractCaptureAction::getTarget)
                .anyMatch(targetPiece -> Objects.equals(targetPiece, attacker));

        return isCapturable;
    }

    static String formatInvalidActionMessage(String action) {
        return String.format("Invalid action format: '%s'", action);
    }

    static String formatUnknownPieceMessage(String action) {
        return String.format("Unknown source piece for action: '%s'", action);
    }

    static String prepare(String action) {
        var command = remove(remove(action, CHECK_CODE), CHECKMATE_CODE);
        return remove(remove(command, GOOD_MOVE_CODE), BAD_MOVE_CODE);
    }
}