package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.isAllLowerCase;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Settable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameBuilder;
import com.agutsul.chess.game.fen.FenGame;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractCastlingActionRule.Castling;

final class FenGameBuilder
        implements GameBuilder {

    private static final Logger LOGGER = getLogger(FenGameBuilder.class);

    private static final String DISABLE_ALL_SYMBOL = "-";

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
        var playerColor = resolveColor(activeColor);

        var game = new FenGame(
                createPlayer(Colors.WHITE),
                createPlayer(Colors.BLACK),
                board,
                playerColor,
                halfMoveClock,
                fullMoveClock
        );

        if (activeCastling != null) {
            if (!DISABLE_ALL_SYMBOL.equals(activeCastling)) {
                disableAllCastlings(board);
                // toggle available castling sides
                enableCastlings(board, activeCastling);
            } else {
                disableAllCastlings(board);
            }
            // save string of enabled castling sides
            game.setParsedCastling(activeCastling);
        }

        if (enPassantPosition != null) {
            if (!DISABLE_ALL_SYMBOL.equals(enPassantPosition)) {
                // reset piece position and perform piece 'big move' action
                // to fill journal properly because en-passant action is based on journal
                resolveEnPassant(game, playerColor.invert(), enPassantPosition);
            }
            // save "en-passant" position
            game.setParsedEnPassant(enPassantPosition);
        }

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

    private static void enableCastlings(Board board, String castling) {
        for (int i = 0; i < castling.length(); i++) {
            var code = String.valueOf(castling.charAt(i));
            var color = isAllUpperCase(code) ? Colors.WHITE : Colors.BLACK;

            toggleCastling(board, color, Castling.of(code).side(), true);
        }
    }

    private static void disableAllCastlings(Board board) {
        for (var color : Colors.values()) {
            for (var side : Castlingable.Side.values()) {
                toggleCastling(board, color, side, false);
            }
        }
    }

    private static void toggleCastling(Board board, Color color,
                                       Castlingable.Side side, Boolean enabled) {

        Stream.of(Piece.Type.KING, Piece.Type.ROOK)
                .map(pieceType -> board.getPieces(color, pieceType))
                .flatMap(Collection::stream)
                .filter(piece -> piece instanceof Settable)
                .map(piece -> (Settable) piece)
                .forEach(piece -> piece.set(side, enabled));
    }

    private static void resolveEnPassant(AbstractPlayableGame game,
                                         Color color, String positionCode) {
        var board = game.getBoard();

        var position = positionOf(positionCode);
        var pawnPiece = board.getPieces(color, Piece.Type.PAWN).stream()
                .filter(pawn -> Objects.equals(pawn.getPosition().x(), position.x()))
                .findFirst()
                .get();

        var targetPosition = Position.codeOf(pawnPiece.getPosition());
        var sourcePosition = Position.codeOf(position.x(), position.y() - pawnPiece.getDirection());

        // reset piece position back to source to be able to perform action
        ((Settable) pawnPiece).set(Action.Type.EN_PASSANT, Pair.of(sourcePosition, targetPosition));
        // clear early calculated piece positions to be able to find pawn on source position
        ((Observable) board).notifyObservers(new ClearPieceDataEvent(color));

        // perform piece 'big move' action to fill journal properly
        try {
            var command = new PerformActionCommand(game.getPlayer(color), board, game);
            command.setSource(sourcePosition);
            command.setTarget(targetPosition);

            command.execute();
        } catch (Exception e) {
            LOGGER.error("Unable to execute action", e);
            throw new IllegalStateException(e);
        }
    }
}