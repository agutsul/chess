package com.agutsul.chess.player.observer;

import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ArrayUtils.getLength;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.AbstractRequestEvent;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public abstract class AbstractPlayerInputObserver
        implements Observer {

    private static final String UNDO_COMMAND = "undo";

    private static final String UNKNOWN_PROMOTION_PIECE_TYPE_MESSAGE = "Unknown promotion piece type";
    private static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process";
    private static final String INVALID_ACTION_FORMAT_MESSAGE= "Invalid action format";

    protected static final Map<String, Piece.Type> PROMOTION_TYPES =
            Stream.of(KNIGHT, BISHOP, ROOK, QUEEN)
                    .collect(toMap(Piece.Type::code, identity()));

    protected final Player player;
    protected final Game game;

    private final Logger logger;

    protected AbstractPlayerInputObserver(Logger logger, Player player, Game game) {
        this.logger = logger;
        this.player = player;
        this.game = game;
    }

    @Override
    public final void observe(Event event) {
        if (!(event instanceof AbstractRequestEvent)) {
            return;
        }

        var requestEvent = (AbstractRequestEvent) event;
        if (!Objects.equals(requestEvent.getColor(), this.player.getColor())) {
            return;
        }

        if (requestEvent instanceof RequestPlayerActionEvent) {
            process((RequestPlayerActionEvent) requestEvent);
        } else if (requestEvent instanceof RequestPromotionPieceTypeEvent) {
            process((RequestPromotionPieceTypeEvent) requestEvent);
        }
    }

    protected abstract String getActionCommand();

    protected abstract String getPromotionPieceType();

    protected void process(RequestPromotionPieceTypeEvent event) {
        var selectedType = getPromotionPieceType();

        logger.debug("Processing selected pawn promotion type '{}'", selectedType);

        var pieceType = PROMOTION_TYPES.get(selectedType);
        if (pieceType == null) {
            throw new IllegalActionException(
                    String.format("%s: '%s'", UNKNOWN_PROMOTION_PIECE_TYPE_MESSAGE, selectedType)
            );
        }

        var action = event.getAction();
        // callback to origin action to continue processing
        action.observe(new PromotionPieceTypeEvent(this.player, pieceType));
    }

    protected void process(RequestPlayerActionEvent event) {
        var command = getActionCommand();

        logger.debug("Processing player '{}' command '{}'",
                this.player.getName(),
                command
        );

        if (UNDO_COMMAND.equalsIgnoreCase(command)) {
            processUndoCommand(this.player);
        } else if (contains(command, SPACE)) {
            processActionCommand(this.player, command);
        } else {
            throw new IllegalActionException(
                    String.format("%s: '%s'", UNABLE_TO_PROCESS_MESSAGE, command)
            );
        }
    }

    private void processUndoCommand(Player player) {
        ((Observable) this.game).notifyObservers(new PlayerCancelActionEvent(player));
    }

    private void processActionCommand(Player player, String command) {
        var positions = split(command, SPACE);
        if (getLength(positions) != 2) {
            throw new IllegalActionException(
                    String.format("%s: '%s'", INVALID_ACTION_FORMAT_MESSAGE, command)
            );
        }

        var action = new PlayerActionEvent(player, positions[0], positions[1]);
        ((Observable) this.game).notifyObservers(action);
    }
}