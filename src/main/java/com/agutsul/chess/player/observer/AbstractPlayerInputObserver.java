package com.agutsul.chess.player.observer;

import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static java.util.Collections.unmodifiableMap;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.AbstractRequestEvent;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerDefeatActionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionEvent;
import com.agutsul.chess.player.event.PlayerExitActionEvent;
import com.agutsul.chess.player.event.PlayerWinActionEvent;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public abstract class AbstractPlayerInputObserver
        implements Observer {

    protected static final String UNDO_COMMAND =   "undo";
    protected static final String DRAW_COMMAND =   "draw";
    protected static final String WIN_COMMAND  =   "win";
    protected static final String DEFEAT_COMMAND = "defeat";
    protected static final String EXIT_COMMAND =   "exit";

    private static final String UNKNOWN_PROMOTION_PIECE_TYPE_MESSAGE = "Unknown promotion piece type";
    private static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process";
    private static final String INVALID_ACTION_FORMAT_MESSAGE= "Invalid action format";

    protected static final Map<String, Piece.Type> PROMOTION_TYPES =
            Stream.of(KNIGHT, BISHOP, ROOK, QUEEN)
                    .collect(toMap(Piece.Type::code, identity()));

    private final Map<Class<? extends Event>, Consumer<Event>> processors;

    protected final Player player;
    protected final Game game;

    private final Logger logger;

    protected AbstractPlayerInputObserver(Logger logger, Player player, Game game) {
        this.logger = logger;
        this.player = player;
        this.game = game;
        this.processors = createEventProcessors();
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

        var processor = this.processors.get(requestEvent.getClass());
        if (processor != null) {
            processor.accept(requestEvent);
        }
    }

    protected abstract String getActionCommand();

    protected abstract String getPromotionPieceType();

    protected void process(RequestPromotionPieceTypeEvent event) {
        var selectedType = upperCase(strip(getPromotionPieceType()));

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
        var command = strip(getActionCommand());

        logger.debug("Processing player '{}' command '{}'",
                this.player.getName(),
                String.valueOf(command)
        );

        try {
            var eventFactory = PlayerActionEventFactory.of(command);
            if (eventFactory != null) {
                notifyGameEvent(eventFactory.create(this.player));
            } else if (contains(command, SPACE)) {
                processActionCommand(this.player, command);
            } else {
                throw new IllegalActionException(
                        String.format("%s: '%s'", UNABLE_TO_PROCESS_MESSAGE, command)
                );
            }
        } catch (Exception e) {
            logger.error("Processing player action failed", e);
            notifyExceptionEvent(e.getMessage());

            // re-ask player action
            notifyBoardEvent(event);
        }
    }

    private Map<Class<? extends Event>, Consumer<Event>> createEventProcessors() {
        var processors = new HashMap<Class<? extends Event>, Consumer<Event>>();

        processors.put(RequestPlayerActionEvent.class, e -> process((RequestPlayerActionEvent) e));
        processors.put(RequestPromotionPieceTypeEvent.class, e -> process((RequestPromotionPieceTypeEvent) e));

        return unmodifiableMap(processors);
    }

    private void processActionCommand(Player player, String command) {
        var positions = split(command, SPACE);
        if (getLength(positions) != 2) {
            throw new IllegalActionException(
                    String.format("%s: '%s'", INVALID_ACTION_FORMAT_MESSAGE, command)
            );
        }

        notifyGameEvent(new PlayerActionEvent(player, positions[0], positions[1]));
    }

    private void notifyBoardEvent(Event event) {
        var board = ((AbstractPlayableGame) this.game).getBoard();
        ((Observable) board).notifyObservers(event);
    }

    private void notifyGameEvent(Event event) {
        ((Observable) this.game).notifyObservers(event);
    }

    private void notifyExceptionEvent(String message) {
        // display error message to player
        notifyGameEvent(new PlayerActionExceptionEvent(message));
        sleepQuietly(Duration.ofMillis(1));
    }

    private enum PlayerActionEventFactory {
        UNDO_MODE(UNDO_COMMAND,     player -> new PlayerCancelActionEvent(player)),
        DRAW_MODE(DRAW_COMMAND,     player -> new PlayerDrawActionEvent(player)),
        WIN_MODE(WIN_COMMAND,       player -> new PlayerWinActionEvent(player)),
        DEFEAT_MODE(DEFEAT_COMMAND, player -> new PlayerDefeatActionEvent(player)),
        EXIT_MODE(EXIT_COMMAND,     player -> new PlayerExitActionEvent(player));

        private static final Map<String,PlayerActionEventFactory> MODES =
                Stream.of(values()).collect(toMap(PlayerActionEventFactory::command, identity()));

        private String command;
        private Function<Player,Event> function;

        PlayerActionEventFactory(String command, Function<Player,Event> function) {
            this.command = command;
            this.function = function;
        }

        public static PlayerActionEventFactory of(String command) {
            return MODES.get(lowerCase(command));
        }

        public Event create(Player player) {
            return function.apply(player);
        }

        private String command() {
            return command;
        }
    }
}