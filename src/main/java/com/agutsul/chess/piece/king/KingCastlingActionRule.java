package com.agutsul.chess.piece.king;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.action.AbstractCastlingActionRule;
import com.agutsul.chess.rule.action.CastlingActionRule;

final class KingCastlingActionRule<COLOR extends Color,
                                   KING  extends KingPiece<COLOR>,
                                   ROOK  extends RookPiece<COLOR>>
        extends AbstractCastlingActionRule<COLOR,KING,ROOK,
                                           PieceCastlingAction<COLOR,KING,ROOK>>
        implements CastlingActionRule<COLOR,KING,ROOK,
                                      PieceCastlingAction<COLOR,KING,ROOK>> {

    KingCastlingActionRule(Board board) {
        super(board, new KingCastlingAlgo<>(board));
    }

    @Override
    public Collection<PieceCastlingAction<COLOR,KING,ROOK>> evaluate(KING king) {

        @SuppressWarnings("unchecked")
        var actions = Stream.of(algo.calculate(king))
                .flatMap(Collection::stream)
                .flatMap(castling -> Stream.of(board.getPosition(castling.getRookSource(), king.getPosition().y()))
                        .flatMap(Optional::stream)
                        .map(position -> board.getPiece(position))
                        .flatMap(Optional::stream)
                        .map(piece -> createAction(castling, king, (ROOK) piece))
                )
                .toList();

        return actions;
    }
}