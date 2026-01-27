package com.agutsul.chess.game.ai;

import static com.agutsul.chess.piece.Piece.Type.KING;
import static com.agutsul.chess.piece.Piece.Type.PAWN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.board.event.CopyVisitedPositionsEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.SimulateActionCommand;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

public final class SimulationGame
        extends AbstractPlayableGame
        implements Closeable {

    private static final Logger LOGGER = getLogger(SimulationGame.class);

    private static final Set<Piece.Type> PIECE_TYPES = EnumSet.of(PAWN, ROOK, KING);

    private final Color color;
    private final Action<?> originAction;

    public SimulationGame(Board board, Journal<ActionMemento<?,?>> journal,
                          ForkJoinPool forkJoinPool, Color activeColor, Action<?> action) {

        this(playerOf(Colors.WHITE), playerOf(Colors.BLACK),
                copyBoard(board), new JournalImpl(journal),
                forkJoinPool, activeColor, action
        );
    }

    private SimulationGame(Player whitePlayer, Player blackPlayer,
                           Board board, Journal<ActionMemento<?,?>> journal,
                           ForkJoinPool forkJoinPool, Color activeColor,
                           Action<?> action) {

        super(LOGGER, whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal),
                new GameContext(forkJoinPool)
        );

        this.color = activeColor;
        this.originAction = action;

        setCurrentPlayer(getPlayer(activeColor));
    }

    public Color getColor() {
        return this.color;
    }

    public Action<?> getAction() {
        return this.originAction;
    }

    @Override
    public void run() {
        LOGGER.info("Simulate '{}' action '{}'", getCurrentPlayer().getColor(), getAction());

        try {
            var command = new SimulateActionCommand(this, getAction());
            command.execute();

            if (hasNext()) {
                setCurrentPlayer(next());
            }
        } catch (IllegalActionException | IllegalStateException | IllegalPositionException e) {
            var boardState = getBoard().getState();

            LOGGER.error("{}{}", lineSeparator(), getBoard());
            LOGGER.error("{}: Game simulation exception('{}'), board state '{}', journal '{}': {}",
                    getCurrentPlayer().getColor(), getAction(), boardState, getJournal(),
                    getStackTrace(e)
            );

            if (!boardState.isType(BoardState.Type.TIMEOUT)) {
                notifyObservers(new GameExceptionEvent(this, e));
            }
        }
    }

    @Override
    public void close() throws IOException {
        notifyBoardObservers(new GameOverEvent(this));
    }

    private static Board copyBoard(Board originBoard) {
        var boardBuilder = new PositionedBoardBuilder();
        // copy board pieces
        originBoard.getPieces().forEach(piece ->
            boardBuilder.withPiece(piece.getType(), piece.getColor(), piece.getPosition())
        );

        var board = boardBuilder.build();
        board.setState(originBoard.getState());

        // copy piece visited positions to properly resolve en-passant and castling actions
        var observableBoard = (Observable) board;
        Stream.of(PIECE_TYPES)
            .flatMap(Collection::stream)
            .map(pieceType -> originBoard.getPieces(pieceType))
            .flatMap(Collection::stream)
            .map(CopyVisitedPositionsEvent::new)
            .forEach(event -> observableBoard.notifyObservers(event));

        return board;
    }
}