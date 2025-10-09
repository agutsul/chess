package com.agutsul.chess.piece.king;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.action.AbstractCastlingActionRule;
import com.agutsul.chess.rule.action.CastlingActionRule;

final class KingCastlingActionRule<COLOR extends Color,
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

        @SuppressWarnings("unchecked")
        var actions = Stream.of(board.getPieces(king.getColor(), Piece.Type.ROOK))
                .flatMap(Collection::stream)
                .map(rook -> super.evaluate((KingPiece<COLOR>) king, (RookPiece<COLOR>) rook))
                .flatMap(Collection::stream)
                .collect(toList());

        return actions;
    }
}