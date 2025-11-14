package com.agutsul.chess.piece.rook;

import static java.util.stream.Collectors.toList;

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

final class RookCastlingActionRule<COLOR extends Color,
                                   ROOK extends RookPiece<COLOR>,
                                   KING extends KingPiece<COLOR>>
        extends AbstractCastlingActionRule<COLOR,ROOK,KING,
                                           PieceCastlingAction<COLOR,ROOK,KING>>
        implements CastlingActionRule<COLOR,ROOK,KING,
                                      PieceCastlingAction<COLOR,ROOK,KING>> {

    RookCastlingActionRule(Board board) {
        super(board);
    }

    @Override
    public Collection<PieceCastlingAction<COLOR,ROOK,KING>> evaluate(ROOK rook) {

        @SuppressWarnings("unchecked")
        var actions = Stream.of(board.getKing(rook.getColor()))
                .flatMap(Optional::stream)
                .map(king -> super.evaluate((KingPiece<COLOR>) king, (RookPiece<COLOR>) rook))
                .flatMap(Collection::stream)
                .collect(toList());

        return actions;
    }
}