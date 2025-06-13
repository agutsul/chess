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
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.ActionTimeoutException;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.PlayerCommand;
import com.agutsul.chess.player.event.AbstractRequestEvent;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public abstract class AbstractPlayerInputObserver
        extends AbstractPlayerObserver {

    protected final Player player;

    protected AbstractPlayerInputObserver(Player player, Game game) {
        super(game);
        this.player = player;
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

        super.observe(event);
    }

    protected abstract String getActionCommand();

    protected abstract String getPromotionPieceType();

    @Override
    protected Observer createObserver() {
        return new CompositeEventObserver(
                new RequestPlayerActionObserver(),
                new RequestPromotionPieceTypeObserver()
        );
    }

    protected final void notifyExceptionEvent(String message) {
        // display error message to player
        notifyGameEvent(new PlayerActionExceptionEvent(message));
        sleepQuietly(Duration.ofMillis(1));
    }

    protected class RequestPromotionPieceTypeObserver
            extends AbstractEventObserver<RequestPromotionPieceTypeEvent> {

        private static final Logger LOGGER = getLogger(RequestPromotionPieceTypeObserver.class);

        private static final String UNKNOWN_PROMOTION_PIECE_TYPE_MESSAGE =
                "Unknown promotion piece type";

        private static final Map<String, Piece.Type> PROMOTION_TYPES =
                Stream.of(KNIGHT, BISHOP, ROOK, QUEEN)
                        .collect(toMap(Piece.Type::code, identity()));

        public RequestPromotionPieceTypeObserver() {}

        @Override
        protected void process(RequestPromotionPieceTypeEvent event) {
            var selectedType = upperCase(strip(getPromotionPieceType()));

            LOGGER.debug("Processing selected pawn promotion type '{}'", selectedType);

            var pieceType = PROMOTION_TYPES.get(selectedType);
            if (pieceType == null) {
                throw new IllegalActionException(String.format("%s: '%s'",
                        UNKNOWN_PROMOTION_PIECE_TYPE_MESSAGE, selectedType
                ));
            }

            var action = event.getAction();
            // callback to origin action to continue processing
            action.observe(new PromotionPieceTypeEvent(player, pieceType));
        }
    }

    protected class RequestPlayerActionObserver
            extends AbstractEventObserver<RequestPlayerActionEvent> {

        private static final Logger LOGGER = getLogger(RequestPlayerActionObserver.class);

        private static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process";
        private static final String INVALID_ACTION_FORMAT_MESSAGE= "Invalid action format";

        public RequestPlayerActionObserver() {}

        @Override
        protected void process(RequestPlayerActionEvent event) {
            try {
                var command = getActionCommand();

                var eventFactory = PlayerActionEventFactory.of(command);
                if (eventFactory != null) {
                    notifyGameEvent(eventFactory.create(player));
                } else if (contains(command, SPACE)) {
                    processActionCommand(command);
                } else {
                    throw new IllegalActionException(String.format("%s: '%s'",
                            UNABLE_TO_PROCESS_MESSAGE, command
                    ));
                }
            } catch (ActionTimeoutException e) {
                var message = String.format("%s: Player '%s' action timeout",
                        player.getColor(), player
                );

                LOGGER.error(message, e);
                throw e;
            } catch (IllegalActionException e) {
                var message = String.format("%s: Player '%s' action failed",
                        player.getColor(), player
                );

                LOGGER.error(message, e);
                notifyExceptionEvent(e.getMessage());

                // re-ask player action
                notifyBoardEvent(event);
            }
        }

        private void processActionCommand(String command) {
            var positions = split(lowerCase(command), SPACE);
            if (getLength(positions) != 2) {
                throw new IllegalActionException(String.format("%s: '%s'",
                        INVALID_ACTION_FORMAT_MESSAGE, command
                ));
            }

            notifyGameEvent(new PlayerActionEvent(player, strip(positions[0]), strip(positions[1])));
        }

        private enum PlayerActionEventFactory {
            UNDO_MODE(PlayerCommand.UNDO,     player -> new PlayerCancelActionEvent(player)),
            DRAW_MODE(PlayerCommand.DRAW,     player -> new PlayerTerminateActionEvent(player, Type.DRAW)),
            WIN_MODE(PlayerCommand.WIN,       player -> new PlayerTerminateActionEvent(player, Type.WIN)),
            DEFEAT_MODE(PlayerCommand.DEFEAT, player -> new PlayerTerminateActionEvent(player, Type.DEFEAT)),
            EXIT_MODE(PlayerCommand.EXIT,     player -> new PlayerTerminateActionEvent(player, Type.EXIT));

            private static final Map<String,PlayerActionEventFactory> MODES =
                    Stream.of(values()).collect(toMap(PlayerActionEventFactory::command, identity()));

            private String command;
            private Function<Player,Event> function;

            PlayerActionEventFactory(PlayerCommand command, Function<Player,Event> function) {
                this.command = command.code();
                this.function = function;
            }

            public static PlayerActionEventFactory of(String command) {
                return MODES.get(strip(lowerCase(command)));
            }

            public Event create(Player player) {
                return function.apply(player);
            }

            private String command() {
                return command;
            }
        }
    }
}