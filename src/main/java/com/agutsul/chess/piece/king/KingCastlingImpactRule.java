package com.agutsul.chess.piece.king;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.impact.PieceCastlingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.impact.castling.AbstractCastlingImpactRule;

final class KingCastlingImpactRule<COLOR extends Color,
                                   KING  extends KingPiece<COLOR>,
                                   ROOK  extends RookPiece<COLOR>>
        extends AbstractCastlingImpactRule<COLOR,KING,ROOK,
                                           PieceCastlingImpact<COLOR,KING,ROOK>> {

    KingCastlingImpactRule(Board board) {
        super(board, new KingCastlingAlgo<>(board));
    }

    @Override
    public Collection<PieceCastlingImpact<COLOR,KING,ROOK>> evaluate(KING king) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(algo.calculate(king))
                .flatMap(Collection::stream)
                .flatMap(castling -> Stream.of(board.getPosition(castling.getRookSource(), king.getPosition().y()))
                        .flatMap(Optional::stream)
                        .map(position -> board.getPiece(position))
                        .flatMap(Optional::stream)
                        .map(piece -> createImpact(castling, king, (ROOK) piece))
                )
                .toList();

        return impacts;
    }
}