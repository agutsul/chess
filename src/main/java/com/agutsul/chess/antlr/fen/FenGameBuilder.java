package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.Castlingable.Castlings.KING_SIDE;
import static com.agutsul.chess.Castlingable.Castlings.QUEEN_SIDE;
import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static com.agutsul.chess.position.Position.codeOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isAllLowerCase;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.Castlingable.Side;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.board.event.ClearCachedDataEvent;
import com.agutsul.chess.board.event.ResetPawnMoveActionEvent;
import com.agutsul.chess.board.event.SetCastlingableSideEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameBuilder;
import com.agutsul.chess.game.fen.FenGame;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

final class FenGameBuilder
        implements GameBuilder<FenGame<?>> {

    static final String INVALID_LINE_FORMAT = "Unsupported board line: '%s'";
    static final String INVALID_LINES_NUMBER_FORMAT = "Unsupported board lines number: '%s'";
    static final String INVALID_COLOR_FORMAT = "Unsupported active player color: '%s'";
    static final String INVALID_CASTLING_FORMAT = "Unsupported castling: '%s'";
    static final String INVALID_ENPASSANT_FORMAT = "Unsupported en-passante: '%s'";
    static final String INVALID_ENPASSANT_POSITION_FORMAT = "Unsupported en-passante position: '%s'";
    static final String INVALID_HALF_MOVES_FORMAT = "Unsupported half moves: '%s'";
    static final String INVALID_FULL_MOVES_FORMAT = "Unsupported full moves: '%s'";
    static final String INVALID_PIECE_TYPE = "Unsupported piece type: '%s'";

    static final String UNSET_ENPASSANT_MESSAGE = "En-passant enabled but not set";

    static final String DISABLE_ALL_SYMBOL = "-";

    private static final String LINE_PATTERN       = "([p,P,n,N,b,B,r,R,q,Q,k,K,1-8]){1,8}";
    private static final String COLOR_PATTERN      = "([w,b]){1}";
    private static final String CASTLING_PATTERN   = "([q,Q,k,K]){1,4}";
    private static final String ENPASSANT_PATTERN  = "([a-h]{1}[3,6]{1}){1}";

    private final Pattern linePattern;
    private final Pattern colorPattern;
    private final Pattern castlingPattern;
    private final Pattern enPassantPattern;

    private final List<String> parsedBoardLines;

    private String activeColor;
    private String activeCastling;
    private String activeEnPassant;
    private String enPassantPosition;

    private int halfMoveClock;
    private int fullMoveClock;

    FenGameBuilder() {
        this.linePattern = compile(LINE_PATTERN);
        this.colorPattern = compile(COLOR_PATTERN);
        this.castlingPattern = compile(CASTLING_PATTERN);
        this.enPassantPattern = compile(ENPASSANT_PATTERN);
        this.parsedBoardLines = new ArrayList<>();
    }

    @Override
    public FenGame<?> build() {

        if (nonNull(activeEnPassant) && isNull(enPassantPosition)) {
            throw new IllegalArgumentException(UNSET_ENPASSANT_MESSAGE);
        }

        if (parsedBoardLines.size() != Position.MAX) {
            throw createArgumentException(INVALID_LINES_NUMBER_FORMAT, join(parsedBoardLines, "/"));
        }

        var playerColor = resolveColor(activeColor);

        // reverse parsed lines to process board from position(0,0) and up to position(7,7)
        var board = createBoard(parsedBoardLines.reversed());

        // by default all castling sides are enabled
        if (nonNull(activeCastling)) {
            if (activeCastling.length() != 4) {
                // in case when only some should be enabled => disable all
                disableAllCastlings(board);
                // toggle available castling sides
                enableCastlings(board, activeCastling);
            }
        } else {
            disableAllCastlings(board);
        }

        var game = new FenGame<>(
                playerOf(Colors.WHITE), playerOf(Colors.BLACK),
                board, playerColor, halfMoveClock, fullMoveClock
        );

        if (nonNull(activeCastling)) {
            // save string of enabled castling sides
            game.setParsedCastling(activeCastling);
        }

        if (nonNull(activeEnPassant)) {
            // reset piece position and perform piece 'big move' action
            // to fill journal properly because en-passant action is based on journal
            enableEnPassant(game, playerColor.invert(), positionOf(enPassantPosition));
            // save "en-passant" position
            game.setParsedEnPassant(enPassantPosition);
        }

        return game;
    }

    GameBuilder<FenGame<?>> addBoardLine(String line) {
        if (isBlank(line)) {
            throw createArgumentException(INVALID_LINE_FORMAT, line);
        }

        var matcher = this.linePattern.matcher(line);
        if (!matcher.matches()) {
            throw createArgumentException(INVALID_LINE_FORMAT, line);
        }

        this.parsedBoardLines.add(line);
        return this;
    }

    GameBuilder<FenGame<?>> withActiveColor(String color) {
        if (isBlank(color)) {
            throw createArgumentException(INVALID_COLOR_FORMAT, color);
        }

        var matcher = this.colorPattern.matcher(lowerCase(color));
        if (!matcher.matches()) {
            throw createArgumentException(INVALID_COLOR_FORMAT, color);
        }

        this.activeColor = color;
        return this;
    }

    GameBuilder<FenGame<?>> withCastling(String castling) {
        if (DISABLE_ALL_SYMBOL.equals(castling)) {
            // skip setting castling when it is disabled
            return this;
        }

        if (isBlank(castling)) {
            throw createArgumentException(INVALID_CASTLING_FORMAT, castling);
        }

        for (int i = 0; i < castling.length(); i++) {
            var matcher = this.castlingPattern.matcher(String.valueOf(castling.charAt(i)));
            if (!matcher.matches()) {
                throw createArgumentException(INVALID_CASTLING_FORMAT, castling);
            }
        }

        this.activeCastling = castling;
        return this;
    }

    GameBuilder<FenGame<?>> withEnPassant(String enPassant) {
        if (DISABLE_ALL_SYMBOL.equals(enPassant)) {
            // skip setting enPassant when it is disabled
            return this;
        }

        if (isBlank(enPassant)) {
            throw createArgumentException(INVALID_ENPASSANT_FORMAT, enPassant);
        }

        var matcher = this.enPassantPattern.matcher(enPassant);
        if (!matcher.matches()) {
            throw createArgumentException(INVALID_ENPASSANT_FORMAT, enPassant);
        }

        this.activeEnPassant = enPassant;
        return this;
    }

    GameBuilder<FenGame<?>> withEnPassantPosition(String enPassantPosition) {
        var matcher = this.enPassantPattern.matcher(enPassantPosition);
        if (!matcher.matches()) {
            throw createArgumentException(INVALID_ENPASSANT_POSITION_FORMAT, enPassantPosition);
        }

        this.enPassantPosition = enPassantPosition;
        return this;
    }

    GameBuilder<FenGame<?>> withHalfMoves(int halfMoves) {
        if (halfMoves < 0) {
            throw createArgumentException(INVALID_HALF_MOVES_FORMAT, String.valueOf(halfMoves));
        }

        this.halfMoveClock = halfMoves;
        return this;
    }

    GameBuilder<FenGame<?>> withFullMoves(int fullMoves) {
        if (fullMoves < 1) {
            throw createArgumentException(INVALID_FULL_MOVES_FORMAT, String.valueOf(fullMoves));
        }

        this.fullMoveClock = fullMoves;
        return this;
    }

    private static Board createBoard(List<String> lines) {
        var boardBuilder = new PositionedBoardBuilder();
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
        return switch (lowerCase(pieceCode)) {
        case "p" -> Piece.Type.PAWN;
        case "n" -> Piece.Type.KNIGHT;
        case "r" -> Piece.Type.ROOK;
        case "b" -> Piece.Type.BISHOP;
        case "q" -> Piece.Type.QUEEN;
        case "k" -> Piece.Type.KING;
        default  -> throw createArgumentException(INVALID_PIECE_TYPE, pieceCode);
        };
    }

    private static Color resolveColor(String colorCode) {
        return switch (lowerCase(colorCode)) {
        case "b" -> Colors.BLACK;
        case "w" -> Colors.WHITE;
        default  -> throw createArgumentException(INVALID_COLOR_FORMAT, colorCode);
        };
    }

    private static Castling resolveCastling(String code) {
        return switch(code) {
        case "q","Q" -> QUEEN_SIDE;
        case "k","K" -> KING_SIDE;
        default -> throw createArgumentException(INVALID_CASTLING_FORMAT, code);
        };
    }

    private static void enableCastlings(Board board, String castlingString) {
        for (int i = 0; i < castlingString.length(); i++) {
            var code = String.valueOf(castlingString.charAt(i));
            var castling = resolveCastling(code);

            toggleCastling(board,
                    isAllUpperCase(code) ? Colors.WHITE : Colors.BLACK,
                    castling.side(),
                    true
            );
        }
    }

    private static void disableAllCastlings(Board board) {
        for (var color : Colors.values()) {
            for (var side : Side.values()) {
                toggleCastling(board, color, side, false);
            }
        }
    }

    private static void toggleCastling(Board board, Color color, Side side, boolean enabled) {
        ((Observable) board).notifyObservers(
                new SetCastlingableSideEvent(color, side, enabled)
        );
    }

    private static void enableEnPassant(Game game, Color color, Position position) {
        var board = game.getBoard();

        // find piece in the same column
        var pawnPiece = Stream.of(board.getPieces(color, Piece.Type.PAWN))
                .flatMap(Collection::stream)
                .filter(pawn -> Objects.equals(pawn.getPosition().x(), position.x()))
                .findFirst()
                .map(piece -> (PawnPiece<Color>) piece)
                .get();

        var targetPosition = codeOf(pawnPiece.getPosition());
        var sourcePosition = codeOf(position.x(), position.y() - pawnPiece.getDirection());

        // reset piece position back to source to be able to perform action
        ((Observable) board).notifyObservers(new ResetPawnMoveActionEvent(pawnPiece, sourcePosition));

        // clear early calculated piece positions to be able to find pawn on source position
        ((Observable) board).notifyObservers(new ClearCachedDataEvent(color));

        // perform piece 'big move' action to fill journal properly
        // so, during en-passant calculation 'big move' can be resolved in journal

        var command = new PerformActionCommand(game.getPlayer(color), board, (Observable) game);
        command.setSource(sourcePosition);
        command.setTarget(targetPosition);

        command.execute();
    }

    private static IllegalArgumentException createArgumentException(String format, String arg) {
        return new IllegalArgumentException(String.format(format, arg));
    }
}