package com.agutsul.chess.game.ai;

import static com.agutsul.chess.piece.Piece.Type.KING;
import static com.agutsul.chess.piece.Piece.Type.PAWN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static java.lang.System.lineSeparator;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.board.event.CopyVisitedPositionsEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.SimulateActionCommand;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractPlayableGame;
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

    private static final Set<Piece.Type> PIECE_TYPES = EnumSet.of(PAWN, ROOK, KING);

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
            LOGGER.error("{}{}", lineSeparator(), String.valueOf(getBoard()));
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
        var boardBuilder = new PositionedBoardBuilder();
        // copy board pieces
        for (var piece : origin.getPieces()) {
            boardBuilder.withPiece(piece.getType(), piece.getColor(), piece.getPosition());
        }

        var board = boardBuilder.build();
        board.setState(origin.getState());

        // copy piece visited positions to properly resolve en-passant and castling actions
        var observableBoard = (Observable) board;
        PIECE_TYPES.stream()
            .map(pieceType -> origin.getPieces(pieceType))
            .flatMap(Collection::stream)
            .map(CopyVisitedPositionsEvent::new)
            .forEach(event -> observableBoard.notifyObservers(event));

        return board;
    }
}