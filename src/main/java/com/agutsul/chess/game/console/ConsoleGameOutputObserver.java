package com.agutsul.chess.game.console;

import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.format;
import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static com.agutsul.chess.journal.JournalFormatter.format;
import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static java.lang.System.lineSeparator;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionCancellingEvent;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.board.state.FoldRepetitionBoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.AbstractObserverProxy;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalFormatter.Mode;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public final class ConsoleGameOutputObserver
        extends AbstractObserverProxy {

    private static final String DRAW_MESSAGE = "Draw";
    private static final String ACTION_MESSAGE = "Action";
    private static final String GAME_OVER_MESSAGE = "Game over";
    private static final String DURATION_MESSAGE = "Duration (minutes)";
    private static final String BOARD_STATE_MESSAGE = "Board state";
    private static final String LINE = "-".repeat(50);

    public ConsoleGameOutputObserver(Game game) {
        super(new CompositeEventObserver(
                new GameStartedConsoleObserver(),
                new GameOverConsoleObserver(),
                new GameTimeoutTerminationConsoleObserver(),
                new BoardStateNotificationConsoleObserver(),
                new RequestPlayerActionConsoleObserver(),
                new RequestPromotionPieceTypeConsoleObserver(),
                new ActionPerformedConsoleObserver(game),
                new ActionExecutionConsoleObserver(game),
                new ActionCancelledConsoleObserver(game),
                new ActionCancellingConsoleObserver(game),
                new ActionTerminatedConsoleObserver(game),
                new ActionTerminationConsoleObserver(),
                new PlayerActionExceptionConsoleObserver(),
                new PlayerCancelActionExceptionConsoleObserver(),
                new PlayerTerminateActionExceptionConsoleObserver()
        ));
    }

    private static final class GameStartedConsoleObserver
            extends AbstractEventObserver<GameStartedEvent> {

        private static final String ENTER_ACTION_MESSAGE =
                "Please, enter an action in the following format: '<source_position> <target_position>'.";

        private static final String ENTER_ACTION_EXAMPLE_MESSAGE =
                "For example: 'e2 e4'";

        @Override
        protected void process(GameStartedEvent event) {
            System.out.println(ENTER_ACTION_MESSAGE);
            System.out.println(ENTER_ACTION_EXAMPLE_MESSAGE);

            displayBoard(event.getGame().getBoard());
        }
    }

    private static final class GameOverConsoleObserver
            extends AbstractEventObserver<GameOverEvent> {

        @Override
        protected void process(GameOverEvent event) {
            var game = event.getGame();

            var board = game.getBoard();
            displayBoardState(board.getState());

            System.out.println(LINE);
            displayJournal(game.getJournal());

            System.out.println(LINE);
            displayWinner(game.getWinnerPlayer());

            var finishedAt = game.getFinishedAt() != null
                    ? game.getFinishedAt()
                    : now();

            displayDuration(Duration.between(game.getStartedAt(), finishedAt));
        }
    }

    private static final class GameTimeoutTerminationConsoleObserver
            extends AbstractEventObserver<GameTimeoutTerminationEvent> {

        @Override
        protected void process(GameTimeoutTerminationEvent event) {
            System.out.println(LINE);

            var game = event.getGame();
            var player = game.getCurrentPlayer();

            System.out.println(String.format(
                    "Game timeout for %s player: '%s'",
                    player.getColor(), player.getName()
            ));
        }
    }

    private static final class BoardStateNotificationConsoleObserver
            extends AbstractEventObserver<BoardStateNotificationEvent> {

        @Override
        protected void process(BoardStateNotificationEvent event) {
            displayBoardState(event.getBoardState());

            System.out.println(String.format("%s: %s%s",
                    ACTION_MESSAGE,
                    format(event.getMemento()),
                    lineSeparator()
            ));
        }
    }

    private static final class RequestPlayerActionConsoleObserver
            extends AbstractEventObserver<RequestPlayerActionEvent> {

        @Override
        protected void process(RequestPlayerActionEvent event) {
            System.out.println(String.format("%s: '%s' move:%s",
                    event.getColor(), event.getPlayer(), lineSeparator()
            ));
        }
    }

    private static final class RequestPromotionPieceTypeConsoleObserver
            extends AbstractEventObserver<RequestPromotionPieceTypeEvent> {

        private static final String PROMOTION_PIECE_TYPE_MESSAGE = "Choose promotion piece type:";
        private static final String PROMPT_PROMOTION_PIECE_TYPE_MESSAGE =
                createPromptPromotionPieceTypeMessage(Stream.of(KNIGHT, BISHOP, ROOK, QUEEN));

        @Override
        protected void process(RequestPromotionPieceTypeEvent ignoredEvent) {
            System.out.println(PROMPT_PROMOTION_PIECE_TYPE_MESSAGE);
        }

        private static String createPromptPromotionPieceTypeMessage(Stream<Piece.Type> promotionTypes) {
            var builder = new StringBuilder();
            builder.append(PROMOTION_PIECE_TYPE_MESSAGE).append(lineSeparator());

            promotionTypes.forEach(pieceType ->
                builder.append("'").append(pieceType).append("' - ")
                    .append(pieceType.name()).append(lineSeparator())
            );

            return builder.toString();
        }
    }

    private static final class ActionPerformedConsoleObserver
            extends AbstractEventObserver<ActionPerformedEvent> {

        private final Game game;

        ActionPerformedConsoleObserver(Game game) {
            this.game = game;
        }

        @Override
        protected void process(ActionPerformedEvent ignoredEvent) {
            displayBoard(this.game.getBoard());
        }
    }

    private static final class ActionExecutionConsoleObserver
            extends AbstractEventObserver<ActionExecutionEvent> {

        private final Game game;

        ActionExecutionConsoleObserver(Game game) {
            this.game = game;
        }

        @Override
        protected void process(ActionExecutionEvent event) {
            displayAction(this.game, event.getPlayer(), event.getAction());
        }
    }

    private static final class ActionCancelledConsoleObserver
            extends AbstractEventObserver<ActionCancelledEvent> {

        private final Game game;

        ActionCancelledConsoleObserver(Game game) {
            this.game = game;
        }

        @Override
        protected void process(ActionCancelledEvent ignoredEvent) {
            displayBoard(this.game.getBoard());
        }
    }

    private static final class ActionCancellingConsoleObserver
            extends AbstractEventObserver<ActionCancellingEvent> {

        private final Game game;

        ActionCancellingConsoleObserver(Game game) {
            this.game = game;
        }

        @Override
        protected void process(ActionCancellingEvent event) {
            displayAction(this.game, this.game.getPlayer(event.getColor()), event.getAction());
        }
    }

    private static final class PlayerActionExceptionConsoleObserver
            extends AbstractEventObserver<PlayerActionExceptionEvent> {

        @Override
        protected void process(PlayerActionExceptionEvent event) {
            displayErrorMessage(event.getMessage());
        }
    }

    private static final class PlayerCancelActionExceptionConsoleObserver
            extends AbstractEventObserver<PlayerCancelActionExceptionEvent> {

        @Override
        protected void process(PlayerCancelActionExceptionEvent event) {
            displayErrorMessage(event.getMessage());
        }
    }

    private static final class PlayerTerminateActionExceptionConsoleObserver
            extends AbstractEventObserver<PlayerTerminateActionExceptionEvent> {

        @Override
        protected void process(PlayerTerminateActionExceptionEvent event) {
            displayErrorMessage(event.getMessage());
        }
    }

    private static final class ActionTerminatedConsoleObserver
            extends AbstractEventObserver<ActionTerminatedEvent> {

        private static final List<Type> TERMINATION_TYPES = List.of(Type.EXIT, Type.TIMEOUT);

        private final Game game;

        ActionTerminatedConsoleObserver(Game game) {
            this.game = game;
        }

        @Override
        protected void process(ActionTerminatedEvent event) {
            if (!TERMINATION_TYPES.contains(event.getType())) {
                displayBoard(game.getBoard());
            }
        }
    }

    private static final class ActionTerminationConsoleObserver
            extends AbstractEventObserver<ActionTerminationEvent> {

        private static final List<Type> TERMINATION_TYPES = List.of(Type.EXIT, Type.TIMEOUT);

        @Override
        protected void process(ActionTerminationEvent event) {
            var player = event.getPlayer();

            var eventType = lowerCase(event.getType().name());
            var message = TERMINATION_TYPES.contains(event.getType())
                    ? String.format("%s: '%s' %s", player.getColor(), player.getName(), eventType)
                    : String.format("%s: '%s' asked '%s'", player.getColor(), player.getName(), eventType);

            System.out.println(message);
        }
    }

    // utilities

    private static void displayAction(Game game, Player player, Action<?> action) {
        var actionPlayer = player != null
                ? player
                : game.getCurrentPlayer();

        var journal = game.getJournal();
        var number = (journal.size() / 2) + 1;

        var formattedAction = isCastling(action)
                ? format(createMemento(game.getBoard(), action))
                : String.valueOf(action);

        System.out.println(String.format("%d. %s %s: '%s': %s",
                number,
                actionPlayer.getColor(),
                ACTION_MESSAGE,
                actionPlayer.getName(),
                formattedAction
        ));
    }

    private static void displayBoard(Board board) {
        System.out.println(String.format("%s%s", lineSeparator(), board));
    }

    private static void displayBoardState(BoardState boardState) {
        System.out.println(String.format("%s: %s: %s",
                boardState.getColor(),
                BOARD_STATE_MESSAGE,
                formatBoardState(boardState)
        ));
    }

    private static void displayJournal(Journal<ActionMemento<?,?>> journal) {
        System.out.println(format(journal, Mode.MULTI_LINE));
    }

    private static void displayWinner(Optional<Player> winner) {
        var message = Stream.of(winner)
                .flatMap(Optional::stream)
                .findFirst()
                .map(player -> formatWinnerMessage(player))
                .orElse(DRAW_MESSAGE);

        System.out.println(String.format("%s. %s", GAME_OVER_MESSAGE, message));
    }

    private static void displayDuration(Duration duration) {
        System.out.println(String.format("%s: %s", DURATION_MESSAGE, duration.toMinutes()));
    }

    private static void displayErrorMessage(String message) {
        System.err.println(message);
    }

    private static String formatWinnerMessage(Player player) {
        return String.format("%s wins! Congratulations, '%s' !!!",
                player.getColor(), player
        );
    }

    private static String formatBoardState(BoardState boardState) {
        return switch (boardState) {
        case FoldRepetitionBoardState bs -> String.format("%s(%s)",
                boardState.getType(), format(bs.getActionMemento())
        );
        case InsufficientMaterialBoardState bs -> String.format("%s(%s)",
                boardState.getType(), bs.getPattern()
        );
        case CompositeBoardState bs -> String.format("(%s)",
                bs.getBoardStates().stream()
                    .map(ConsoleGameOutputObserver::formatBoardState)
                    .collect(joining(","))
        );
        default -> String.valueOf(boardState.getType());
        };
    }
}