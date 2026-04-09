package com.agutsul.chess.piece.rook;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.rule.action.AbstractCastlingActionRule;
import com.agutsul.chess.rule.action.CastlingActionRule;

final class RookCastlingActionRule<COLOR extends Color,
                                   ROOK  extends RookPiece<COLOR>,
                                   KING  extends KingPiece<COLOR>>
        extends AbstractCastlingActionRule<COLOR,ROOK,KING,
                                           PieceCastlingAction<COLOR,ROOK,KING>>
        implements CastlingActionRule<COLOR,ROOK,KING,
                                      PieceCastlingAction<COLOR,ROOK,KING>> {

    RookCastlingActionRule(Board board,
                           CastlingPieceAlgo<COLOR,ROOK,Castling> algo) {

        super(board, algo);
    }

    @Override
    public Collection<PieceCastlingAction<COLOR,ROOK,KING>> evaluate(ROOK rook) {

        var actions = Stream.of(board.getKing(rook.getColor()))
                .flatMap(Optional::stream)
                .flatMap(king -> Stream.of(algo.calculate(rook))
                        .flatMap(Collection::stream)
                        .map(castling -> createAction(castling, king, rook))
                )
                .toList();

        return actions;
    }
}