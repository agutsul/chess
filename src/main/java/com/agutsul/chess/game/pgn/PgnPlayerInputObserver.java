package com.agutsul.chess.game.pgn;

import static com.agutsul.chess.game.state.GameState.Type.BLACK_WIN;
import static com.agutsul.chess.game.state.GameState.Type.DRAWN_GAME;
import static com.agutsul.chess.game.state.GameState.Type.WHITE_WIN;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Termination;
import com.agutsul.chess.pgn.action.ActionAdapter;
import com.agutsul.chess.pgn.action.PawnPromotionTypeAdapter;
import com.agutsul.chess.pgn.action.PieceActionAdapter;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class PgnPlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(PgnPlayerInputObserver.class);

    private final ActionIterator actionIterator;

    private final ActionAdapter pieceActionAdapter;
    private final ActionAdapter promotionTypeAdapter;

    PgnPlayerInputObserver(Player player, PgnGame game, List<String> actions) {
        super(LOGGER, player, game);

        this.actionIterator = new ActionIterator(actions);
        this.pieceActionAdapter = new PieceActionAdapter(
                game.getBoard(),
                player.getColor()
        );
        this.promotionTypeAdapter = new PawnPromotionTypeAdapter(
                game.getBoard(),
                player.getColor()
        );
    }

    @Override
    protected String getActionCommand() {
        if (!actionIterator.hasNext()) {
            return finalCommand();
        }

        var action = actionIterator.next();
        return pieceActionAdapter.adapt(action);
    }

    @Override
    protected String getPromotionPieceType() {
        var action = actionIterator.current();
        return promotionTypeAdapter.adapt(action);
    }

    private String finalCommand() {
        var pgnGame = (PgnGame) this.game;

        var gameState = pgnGame.getParsedGameState();
        if (DRAWN_GAME.equals(gameState.getType())) {
            return DRAW_COMMAND;
        }

        var termination = pgnGame.getParsedTermination();
        if (Termination.TIME_FORFEIT.equals(termination)) {
            return DEFEAT_COMMAND;
        }

        if (Termination.ABANDONED.equals(termination)) {
            if (WHITE_WIN.equals(gameState.getType())) {
                return Objects.equals(player.getColor(), Colors.WHITE) ? WIN_COMMAND : DEFEAT_COMMAND;
            }

            if (BLACK_WIN.equals(gameState.getType())) {
                return Objects.equals(player.getColor(), Colors.BLACK) ? WIN_COMMAND : DEFEAT_COMMAND;
            }
        }

        var board = pgnGame.getBoard();
        if (Termination.NORMAL.equals(termination)) {

            var currentState = board.getState();
            if (currentState.isType(BoardState.Type.CHECKED)
                    || currentState.isType(BoardState.Type.INSUFFICIENT_MATERIAL)) {

                return DEFEAT_COMMAND;
            }

            var boardStates = new ArrayList<>(board.getStates());
            if (boardStates.size() < 2) {
                return EXIT_COMMAND;
            }

            var opponentState = boardStates.get(boardStates.size() - 2);
            if (opponentState.isType(BoardState.Type.INSUFFICIENT_MATERIAL)) {
                return WIN_COMMAND;
            }
        }

        return EXIT_COMMAND;
    }

    private static final class ActionIterator
            implements Iterator<String> {

        private final Iterator<String> iterator;

        // temporary cache action to be used later for promotion action
        // to resolve user selected promotion piece type
        private String current;

        ActionIterator(List<String> actions) {
            this.iterator = actions.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public String current() {
            return this.current;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                return null;
            }

            var action = this.iterator.next();
            this.current = action;

            return action;
        }
    }
}