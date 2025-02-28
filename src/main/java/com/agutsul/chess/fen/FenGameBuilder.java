package com.agutsul.chess.fen;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.isAllLowerCase;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameBuilder;
import com.agutsul.chess.game.fen.FenGame;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.position.Position;

final class FenGameBuilder
        implements GameBuilder {

    private List<String> parsedBoardLines = new ArrayList<>();

    private String activeColor;
    private String activeCastling;
    private String enPassantPosition;

    private int halfMoveClock;
    private int fullMoveClock;

    @Override
    public Game build() {
        // reverse parsed lines to process board from position(0,0) and up to position(7,7)
        var board = createBoard(parsedBoardLines.reversed());

        var game = new FenGame(
                createPlayer(Colors.WHITE),
                createPlayer(Colors.BLACK),
                board,
                resolveColor(activeColor)
        );

        game.setParsedCastling(activeCastling);
        game.setParsedEnPassant(enPassantPosition);
        game.setParsedHalfMoves(halfMoveClock);
        game.setParsedFullMoves(fullMoveClock);

        return game;
    }

    GameBuilder addBoardLine(String line) {
        this.parsedBoardLines.add(line);
        return this;
    }

    GameBuilder withActiveColor(String color) {
        this.activeColor = color;
        return this;
    }

    GameBuilder withCastling(String castling) {
        this.activeCastling = castling;
        return this;
    }

    GameBuilder withEnPassant(String enpassant) {
        this.enPassantPosition = enpassant;
        return this;
    }

    GameBuilder withHalfMoves(int halfMoves) {
        this.halfMoveClock = halfMoves;
        return this;
    }

    GameBuilder withFullMoves(int fullMoves) {
        this.fullMoveClock = fullMoves;
        return this;
    }

    private static Player createPlayer(Color color) {
        return new UserPlayer(
                String.format("%sPlayer", lowerCase(String.valueOf(color))),
                color
        );
    }

    private static Board createBoard(List<String> lines) {
        var boardBuilder = new PositionBoardBuilder();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);

            for (int j = 0, k = 0; j < line.length() && k < Position.MAX; j++) {
                var symbol = String.valueOf(line.charAt(j));

                if (isNumeric(symbol)) {
                    k += toInt(symbol);
                } else if (isAllLowerCase(symbol)) {
                    boardBuilder.withBlackPiece(resolvePieceType(symbol), positionOf(k,i));
                    k++;
                } else if (isAllUpperCase(symbol)) {
                    boardBuilder.withWhitePiece(resolvePieceType(symbol), positionOf(k,i));
                    k++;
                }
            }
        }

        return boardBuilder.build();
    }

    private static Piece.Type resolvePieceType(String pieceCode) {
        switch (lowerCase(pieceCode)) {
        case "p":
            return Piece.Type.PAWN;
        case "n":
            return Piece.Type.KNIGHT;
        case "r":
            return Piece.Type.ROOK;
        case "b":
            return Piece.Type.BISHOP;
        case "q":
            return Piece.Type.QUEEN;
        case "k":
            return Piece.Type.KING;
        default:
            throw new IllegalArgumentException(String.format(
                    "Unsupported piece type: '%s'", pieceCode
            ));
        }
    }

    private static Color resolveColor(String colorCode) {
        switch (lowerCase(colorCode)) {
        case "b":
            return Colors.BLACK;
        case "w":
            return Colors.WHITE;
        default:
            throw new IllegalArgumentException(String.format(
                    "Unsupported player color: '%s'", colorCode
            ));
        }
    }
}