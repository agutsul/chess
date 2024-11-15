package com.agutsul.chess.piece.king;

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

class KingCastlingActionRule<COLOR extends Color,
                             KING extends KingPiece<COLOR>,
                             ROOK extends RookPiece<COLOR>>
        extends AbstractCastlingActionRule<COLOR,KING,ROOK,
                                           PieceCastlingAction<COLOR,KING,ROOK>>
        implements CastlingActionRule<COLOR,KING,ROOK,
                                      PieceCastlingAction<COLOR,KING,ROOK>> {

    KingCastlingActionRule(Board board) {
        super(board);
    }

    @Override
    public Collection<PieceCastlingAction<COLOR,KING,ROOK>> evaluate(KING king) {
        var actions = new ArrayList<PieceCastlingAction<COLOR,KING,ROOK>>();

        Collection<Piece<COLOR>> rooks =
                board.getPieces(king.getColor(), Piece.Type.ROOK);

        for (var rook : rooks) {
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