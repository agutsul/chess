package com.agutsul.chess.player.observer;

import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.split;
import static org.slf4j.LoggerFactory.getLogger;

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

    private static final Logger LOGGER = getLogger(AbstractPlayerInputObserver.class);

    private static final String UNDO_COMMAND = "undo";

    protected static final Map<String, Piece.Type> PROMOTION_TYPES =
            Stream.of(KNIGHT, BISHOP, ROOK, QUEEN)
                    .collect(toMap(Piece.Type::code, identity()));

    protected final Player player;
    protected final Game game;

    public AbstractPlayerInputObserver(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    @Override
    public final void observe(Event event) {
        if (!(event instanceof AbstractRequestEvent)) {
            return;
        }

        var requestEvent = (AbstractRequestEvent) event;
        if (!Objects.equals(requestEvent.getColor(), player.getColor())) {
            return;
        }

        if (requestEvent instanceof RequestPlayerActionEvent) {
            process((RequestPlayerActionEvent) requestEvent);
        } else if (requestEvent instanceof RequestPromotionPieceTypeEvent) {
            process((RequestPromotionPieceTypeEvent) requestEvent);
        }
    }

    protected abstract String getActionCommand(Player player);

    protected abstract String getPieceType();

    protected void process(RequestPromotionPieceTypeEvent event) {
        var selectedType = getPieceType();

        LOGGER.info("Processing selected pawn promotion type '{}'", selectedType);

        var pieceType = PROMOTION_TYPES.get(selectedType);
        if (pieceType == null) {
            throw new IllegalActionException(
                    String.format("Unknown promotion piece type: '%s'", selectedType)
            );
        }

        var action = event.getAction();
        // callback to origin action to continue processing
        action.observe(new PromotionPieceTypeEvent(pieceType));
    }

    protected void process(RequestPlayerActionEvent event) {
        var player = event.getPlayer();
        var command = getActionCommand(player);

        LOGGER.info("Processing player '{}' command '{}'", player.getName(), command);

        if (UNDO_COMMAND.equalsIgnoreCase(command)) {
            processUndoCommand(player);
        } else if (contains(command, SPACE)) {
            processActionCommand(player, command);
        } else {
            throw new IllegalActionException(
                    String.format("Unable to process: '%s'", command)
            );
        }
    }

    private void processUndoCommand(Player player) {
        ((Observable) this.game).notifyObservers(new PlayerCancelActionEvent(player));
    }

    private void processActionCommand(Player player, String command) {
        var positions = split(command, SPACE);
        if (positions == null || positions.length != 2) {
            throw new IllegalActionException(
                    String.format("Invalid action format: %s", command)
            );
        }

        var action = new PlayerActionEvent(player, positions[0], positions[1]);
        ((Observable) this.game).notifyObservers(action);
    }
}