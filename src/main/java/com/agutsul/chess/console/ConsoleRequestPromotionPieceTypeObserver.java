package com.agutsul.chess.console;

import static java.lang.System.lineSeparator;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.EnumSet;
import java.util.Map;

import org.slf4j.Logger;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

class ConsoleRequestPromotionPieceTypeObserver
        extends AbstractConsoleInputReader
        implements Observer {

    private static final Logger LOGGER = getLogger(ConsoleRequestPromotionPieceTypeObserver.class);

    private static final Map<String, Piece.Type> PROMOTION_TYPES =
            EnumSet.of(
                    Piece.Type.KNIGHT,
                    Piece.Type.BISHOP,
                    Piece.Type.ROOK,
                    Piece.Type.QUEEN
            ).stream()
            .collect(toMap(Piece.Type::toString, identity()));

    @Override
    public void observe(Event event) {
        if (event instanceof RequestPromotionPieceTypeEvent) {
            process((RequestPromotionPieceTypeEvent) event);
        }
    }

    private void process(RequestPromotionPieceTypeEvent event) {
        var selectedType = readPieceType();

        LOGGER.info("Processing selected pawn promotion type '{}'", selectedType);

        var pieceType = PROMOTION_TYPES.get(selectedType);
        if (pieceType == null) {
            throw new IllegalActionException(
                    String.format("Unknown promotion piece type: '%s'", selectedType)
            );
        }

        var action = event.getAction();
        // callback to origin action to continue processing
        action.observe(new PromotionPieceTypeEvent(pieceType));
    }

    private String readPieceType() {
        promptPieceType();

        var input = trimToEmpty(readConsoleInput());
        if (isEmpty(input)) {
            throw new IllegalActionException("Unable to process an empty line");
        }

        return upperCase(input.substring(0, 1));
    }

    private static void promptPieceType() {
        var builder = new StringBuilder();
        builder.append("Choose promotion piece type:").append(lineSeparator());

        for (var pieceType : PROMOTION_TYPES.values()) {
            builder.append("'").append(pieceType).append("' - ");
            builder.append(pieceType.name()).append(lineSeparator());
        }

        System.out.println(builder.toString());
    }
}