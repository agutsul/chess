package com.agutsul.chess.piece.rook;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.impact.PieceCastlingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.impact.castling.AbstractCastlingImpactRule;

final class RookCastlingImpactRule<COLOR extends Color,
                                   ROOK  extends RookPiece<COLOR>,
                                   KING  extends KingPiece<COLOR>>
        extends AbstractCastlingImpactRule<COLOR,ROOK,KING,
                                           PieceCastlingImpact<COLOR,ROOK,KING>> {

    RookCastlingImpactRule(Board board) {
        super(board, new RookCastlingAlgo<>(board));
    }

    @Override
    public Collection<PieceCastlingImpact<COLOR,ROOK,KING>> evaluate(ROOK rook) {

        var impacts = Stream.of(board.getKing(rook.getColor()))
                .flatMap(Optional::stream)
                .flatMap(king -> Stream.of(algo.calculate(rook))
                        .flatMap(Collection::stream)
                        .map(castling -> createImpact(castling, king, rook))
                )
                .toList();

        return impacts;
    }
}