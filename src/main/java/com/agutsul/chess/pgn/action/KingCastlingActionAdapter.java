package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;

import java.util.Objects;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;

final class KingCastlingActionAdapter
        extends AbstractActionAdapter {

    enum CastlingSide {
        KING_SIDE("O-O", 2),
        QUEEN_SIDE("O-O-O", -2);

        private String code;
        private int step;

        CastlingSide(String code, int step) {
            this.code = code;
            this.step = step;
        }

        String code() {
            return code;
        }

        int step() {
            return step;
        }
    }

    private final CastlingSide side;

    KingCastlingActionAdapter(Board board, Color color, CastlingSide side) {
        super(board, color);
        this.side = side;
    }

    @Override
    public String adapt(String action) {
        if (!Objects.equals(side.code(), action)) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var kingPiece = board.getKing(color);
        if (kingPiece.isEmpty()) {
            throw new IllegalActionException(formatUnknownPieceMessage(action));
        }

        var king = kingPiece.get();
        var position = king.getPosition();

        return adapt(king, codeOf(position.x() + side.step(), position.y()));
    }
}