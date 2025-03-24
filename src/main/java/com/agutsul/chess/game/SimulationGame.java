package com.agutsul.chess.game;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionBoardBuilder;
import com.agutsul.chess.board.event.CopyVisitedPositionsEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.SimulateActionCommand;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;

public final class SimulationGame
        extends AbstractPlayableGame
        implements Closeable {

    private static final Logger LOGGER = getLogger(SimulationGame.class);

    private final Action<?> originAction;

    public SimulationGame(Color activeColor,
                          Board board,
                          Journal<ActionMemento<?,?>> journal,
                          Action<?> action) {

        super(LOGGER,
                createPlayer(Colors.WHITE),
                createPlayer(Colors.BLACK),
                copyBoard(board),
                new JournalImpl(journal)
        );

        this.originAction = action;
        this.currentPlayer = getPlayer(activeColor);

        getCurrentPlayer().setState(activeState);
        getOpponentPlayer().setState(lockedState);
    }

    public Action<?> getAction() {
        return this.originAction;
    }

    @Override
    public void run() {
        LOGGER.info("Simulate '{}' action '{}'",
                getCurrentPlayer().getColor(),
                getAction()
        );

        try {
            var command = new SimulateActionCommand(this, getAction());
            command.execute();

            this.currentPlayer = next();

            clearPieceData(getCurrentPlayer().getColor());

            getBoard().setState(evaluateBoardState(getCurrentPlayer()));

        } catch (Throwable throwable) {
            LOGGER.error(System.lineSeparator() + String.valueOf(getBoard()));
            LOGGER.error("{}: Game simulation exception('{}'), board state '{}', journal '{}': {}",
                    getCurrentPlayer().getColor(),
                    this.originAction,
                    getBoard().getState(),
                    getJournal(),
                    getStackTrace(throwable)
            );
        }
    }

    @Override
    public void close() throws IOException {
        notifyBoardObservers(new GameOverEvent(this));
    }

    private static Player createPlayer(Color color) {
        return new UserPlayer(String.format("%s-%s", color, randomUUID()), color);
    }

    private static Board copyBoard(Board origin) {
        var boardBuilder = new PositionBoardBuilder();
        // copy board pieces
        for (var piece : origin.getPieces()) {
            boardBuilder.withPiece(piece.getType(), piece.getColor(), piece.getPosition());
        }

        var board = boardBuilder.build();
        board.setState(origin.getState());

        // copy piece visited positions
        var observableBoard = (Observable) board;
        for (var piece : origin.getPieces(Piece.Type.PAWN)) {
            observableBoard.notifyObservers(new CopyVisitedPositionsEvent(piece));
        }

        return board;
    }
}