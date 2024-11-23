package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;

final class KingCastlingActionAdapter
        extends AbstractActionAdapter {

    enum CastlingSide {
        KING(2),
        QUEEN(-2);

        private int step;

        CastlingSide(int step) {
            this.step = step;
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
        var kingPiece = board.getKing(color);
        if (kingPiece.isEmpty()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var king = kingPiece.get();
        var position = king.getPosition();

        return adapt(king, codeOf(position.x() + side.step(), position.y()));
    }
}