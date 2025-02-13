package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.rook.RookPieceActionRule;
import com.agutsul.chess.piece.rook.RookPieceImpactRule;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

final class RookPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements RookPiece<COLOR> {

    private static final PieceState<?> DISPOSED_STATE = new DisposedCastlingablePieceState<>();

    RookPieceImpl(Board board, COLOR color, String unicode,
                  Position position, int direction) {

        super(board, Piece.Type.ROOK, color, unicode, position, direction,
                new RookPieceActionRule(board),
                new RookPieceImpactRule(board)
        );
    }

    @Override
    public Collection<Action<?>> getActions() {
        // Castling action for rook should have less priority
        // (because by default castling is initiated by the king)
        // and as result be the last in the result collection.
        // Action order influences action auto-detection used by PerformActionCommand.
        var actions = new ArrayList<>(super.getActions());
        actions.sort((action1, action2) -> {
            var type1 = (Action.Type) action1.getType();
            var type2 = (Action.Type) action2.getType();
            // expected order: capture, move, castling
            return type1.compareTo(type2);
        });

        return actions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispose() {
        super.dispose();

        this.currentState = (PieceState<Piece<COLOR>>) DISPOSED_STATE;
    }

    static final class DisposedCastlingablePieceState<PIECE extends RookPiece<?>>
            extends AbstractCastlingablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedCastlingablePieceState.class);

        DisposedCastlingablePieceState() {
            super(new DisposedPieceStateImpl<>());
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.warn("Castling by disabled '{}' to '{}'", piece, position);
            // do nothing
        }
    }
}