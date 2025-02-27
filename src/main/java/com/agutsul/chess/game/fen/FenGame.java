package com.agutsul.chess.game.fen;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.console.ConsoleGameOutputObserver;
import com.agutsul.chess.game.console.ConsolePlayerInputObserver;
import com.agutsul.chess.player.Player;

public final class FenGame
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(FenGame.class);

    private String parsedCastling;
    private String parsedEnPassant;

    private int parsedHalfMoves;
    private int parsedFullMoves;

    public FenGame(Player whitePlayer, Player blackPlayer,
                   Board board, Color color) {

        super(LOGGER, whitePlayer, blackPlayer, board);

        // by default WHITE is active player
        if (Colors.BLACK.equals(color)) {
            this.currentPlayer = next();
        }

        // re-evaluate board state
        evaluateBoardState(getCurrentPlayer());

        ((Observable) board).addObserver(new ConsolePlayerInputObserver(whitePlayer, this));
        ((Observable) board).addObserver(new ConsolePlayerInputObserver(blackPlayer, this));

        addObserver(new ConsoleGameOutputObserver(this));
    }

    public String getParsedCastling() {
        return parsedCastling;
    }

    public void setParsedCastling(String parsedCastling) {
        this.parsedCastling = parsedCastling;
    }

    public String getParsedEnPassant() {
        return parsedEnPassant;
    }

    public void setParsedEnPassant(String parsedEnPassant) {
        this.parsedEnPassant = parsedEnPassant;
    }

    public int getParsedHalfMoves() {
        return parsedHalfMoves;
    }

    public void setParsedHalfMoves(int parsedHalfMoves) {
        this.parsedHalfMoves = parsedHalfMoves;
    }

    public int getParsedFullMoves() {
        return parsedFullMoves;
    }

    public void setParsedFullMoves(int parsedFullMoves) {
        this.parsedFullMoves = parsedFullMoves;
    }
}