package com.agutsul.chess.game.console;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.piece.Piece.isPawn;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionCancellingEvent;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.activity.action.memento.ActionMementoMock;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

@ExtendWith(MockitoExtension.class)
public class ConsoleGameOutputObserverTest implements TestFileReader {

    private static final Player PLAYER = new UserPlayer("white_player", Colors.WHITE);
    private static final Board STANDARD_BOARD = new StandardBoard();

    @AutoClose
    OutputStream outputStream = new ByteArrayOutputStream();

    @AutoClose
    OutputStream errorStream = new ByteArrayOutputStream();

    @AutoClose
    PrintStream originalOut = System.out;

    @AutoClose
    PrintStream originalErr = System.err;

    @Mock
    AbstractPlayableGame game;

    @InjectMocks
    ConsoleGameOutputObserver observer;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @AfterEach
    public void tearDown() {
        // restore System.out & System.err
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testProcessGameStartedEvent() throws URISyntaxException, IOException {
        when(game.getBoard())
            .thenReturn(STANDARD_BOARD);

        observer.observe(new GameStartedEvent(game));
        assertStream("console_game_started_event.txt", outputStream);
    }

    @Test
    void testProcessGameOverDrawEvent() throws URISyntaxException, IOException {
        when(game.getBoard())
            .thenReturn(STANDARD_BOARD);
        when(game.getWinner())
            .thenReturn(Optional.empty());
        when(game.getJournal())
            .thenReturn(new JournalImpl());
        when(game.getStartedAt())
            .thenReturn(LocalDateTime.of(2021, 04, 24, 14, 33, 48, 123456789));
        when(game.getFinishedAt())
            .thenReturn(LocalDateTime.of(2021, 04, 24, 14, 50, 48, 123456789));

        observer.observe(new GameOverEvent(game));
        assertStream("console_game_over_draw_event.txt", outputStream);
    }

    @Test
    void testProcessGameOverWinEvent() throws URISyntaxException, IOException {
        when(game.getBoard())
            .thenReturn(STANDARD_BOARD);
        when(game.getWinner())
            .thenReturn(Optional.of(PLAYER));
        when(game.getJournal())
            .thenReturn(new JournalImpl());
        when(game.getStartedAt())
            .thenReturn(LocalDateTime.of(2021, 04, 24, 14, 33, 48, 123456789));
        when(game.getFinishedAt())
            .thenReturn(LocalDateTime.of(2021, 04, 24, 14, 50, 48, 123456789));

        observer.observe(new GameOverEvent(game));
        assertStream("console_game_over_win_event.txt", outputStream);
    }

    @Test
    void testProcessBoardStateNotificationEvent() throws URISyntaxException, IOException {
        var boardState = defaultBoardState(STANDARD_BOARD, Colors.WHITE);
        var actionMemento = new ActionMementoMock<String,String>(
                Colors.WHITE, Action.Type.MOVE, Piece.Type.PAWN, "e2", "e3"
        );

        observer.observe(new BoardStateNotificationEvent(boardState, actionMemento));
        assertStream("console_board_state_notification_event.txt", outputStream);
    }

    @Test
    void testProcessRequestPlayerActionEvent() throws URISyntaxException, IOException {
        observer.observe(new RequestPlayerActionEvent(PLAYER));
        assertStream("console_request_player_action_event.txt", outputStream);
    }

    @Test
    void testProcessRequestPromotionPieceTypeEvent() throws URISyntaxException, IOException {
        observer.observe(new RequestPromotionPieceTypeEvent(Colors.WHITE, mock(Observer.class)));
        assertStream("console_request_promotion_piece_type_event.txt", outputStream);
    }

    @Test
    void testProcessActionPerformedEvent() throws URISyntaxException, IOException {
        var boardBuilder = new PositionedBoardBuilder();

        var searchedPosition = positionOf("e2");
        for (var piece : STANDARD_BOARD.getPieces()) {
            if (isPawn(piece) && Objects.equals(searchedPosition, piece.getPosition())) {
                continue;
            }

            boardBuilder.withPiece(piece.getType(), piece.getColor(), piece.getPosition());
        }

        boardBuilder.withWhitePawn(positionOf("e3"));

        when(game.getBoard())
            .thenReturn(boardBuilder.build());

        var actionMemento = new ActionMementoMock<String,String>(
                Colors.WHITE, Action.Type.MOVE, Piece.Type.PAWN, "e2", "e3"
        );

        observer.observe(new ActionPerformedEvent(PLAYER, actionMemento));
        assertStream("console_action_performed_event.txt", outputStream);
    }

    @Test
    void testProcessActionExecutionEvent() throws URISyntaxException, IOException {
        when(game.getJournal())
            .thenReturn(new JournalImpl());

        var moveAction = new PieceMoveAction<>(
                (PawnPiece<?>) STANDARD_BOARD.getPiece("e2").get(),
                positionOf("e3")
        );

        observer.observe(new ActionExecutionEvent(PLAYER, moveAction));
        assertStream("console_action_execution_event.txt", outputStream);
    }

    @Test
    void testProcessActionCancelledEvent() throws URISyntaxException, IOException {
        when(game.getBoard())
            .thenReturn(STANDARD_BOARD);

        observer.observe(new ActionCancelledEvent(Colors.WHITE));
        assertStream("console_action_cancelled_event.txt", outputStream);
    }

    @Test
    void testProcessActionCancellingEvent() throws URISyntaxException, IOException {
        when(game.getPlayer(any(Color.class)))
            .thenReturn(PLAYER);
        when(game.getJournal())
            .thenReturn(new JournalImpl());

        var moveAction = new PieceMoveAction<>(
                (PawnPiece<?>) STANDARD_BOARD.getPiece("e2").get(),
                positionOf("e3")
        );

        observer.observe(new ActionCancellingEvent(Colors.WHITE, moveAction));
        assertStream("console_action_cancelling_event.txt", outputStream);
    }

    @Test
    void testProcessPlayerActionExceptionEvent() throws URISyntaxException, IOException {
        observer.observe(new PlayerActionExceptionEvent("test player action error message"));
        assertStream("console_player_action_exception_event.txt", errorStream);
    }

    @Test
    void testProcessPlayerCancelActionExceptionEvent() throws URISyntaxException, IOException {
        var message = "test player cancel action error message";
        observer.observe(new PlayerCancelActionExceptionEvent(message));
        assertStream("console_player_cancel_action_exception_event.txt", errorStream);
    }

    @Test
    void testProcessPlayerTerminateActionExceptionEvent() throws URISyntaxException, IOException {
        var message = "test player terminate action error message";
        observer.observe(new PlayerTerminateActionExceptionEvent(PLAYER, message, Type.DRAW));
        assertStream("console_player_terminate_action_exception_event.txt", errorStream);
    }

    @Test
    void testProcessActionTerminatedEvent() throws URISyntaxException, IOException {
        when(game.getBoard())
            .thenReturn(STANDARD_BOARD);

        observer.observe(new ActionTerminatedEvent(PLAYER, Type.DRAW));
        assertStream("console_action_terminated_event.txt", outputStream);
    }

    @ParameterizedTest(name = "{index}. testProcessActionTerminationEvent({0})")
    @EnumSource(value = Type.class, names = { "DRAW", "EXIT", "TIMEOUT" })
    void testProcessActionTerminationEvent(Type terminationType)
            throws URISyntaxException, IOException {

        observer.observe(new ActionTerminationEvent(PLAYER, terminationType));

        var fileName = String.format(
                "console_action_termination_%s_event.txt",
                lowerCase(terminationType.name())
        );

        assertStream(fileName, outputStream);
    }

    private void assertStream(String file, OutputStream stream)
            throws URISyntaxException, IOException {

        var expected = readFileContent(file);
        var actual = stream.toString();

        assertEquals(expected, actual);
    }
}