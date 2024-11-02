package com.agutsul.chess.piece.rook;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.action.AbstractCastlingActionRule;
import com.agutsul.chess.rule.action.CastlingActionRule;

class RookCastlingActionRule<COLOR extends Color,
                             ROOK extends RookPiece<COLOR>,
                             KING extends KingPiece<COLOR>>
        extends AbstractCastlingActionRule<COLOR, ROOK, KING,
                                           PieceCastlingAction<COLOR, ROOK, KING>>
        implements CastlingActionRule<COLOR, ROOK, KING,
                                           PieceCastlingAction<COLOR, ROOK, KING>> {

    RookCastlingActionRule(Board board) {
        super(board);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<PieceCastlingAction<COLOR, ROOK, KING>> evaluate(ROOK rook) {
        var actions = new ArrayList<PieceCastlingAction<COLOR, ROOK, KING>>();
        for (var king : board.getPieces(rook.getColor(), Piece.Type.KING)) {
            var castlingActions = super.evaluate(
                    (KingPiece<COLOR>) king,
                    (RookPiece<COLOR>) rook
            );

            if (castlingActions.isEmpty()) {
                continue;
            }

            actions.addAll(castlingActions);
        }

        return actions;
    }
}