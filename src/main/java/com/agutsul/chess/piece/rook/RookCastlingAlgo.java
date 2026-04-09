package com.agutsul.chess.piece.rook;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.AbstractCastlingAlgo;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.piece.algo.KingSideCastlingAlgo;
import com.agutsul.chess.piece.algo.QueenSideCastlingAlgo;

final class RookCastlingAlgo<COLOR extends Color,
                             ROOK  extends RookPiece<COLOR>>
        extends AbstractAlgo<ROOK,Castling>
        implements CastlingPieceAlgo<COLOR,ROOK,Castling> {

    private final Collection<AbstractCastlingAlgo<COLOR,KingPiece<COLOR>,ROOK>> algos;

    RookCastlingAlgo(Board board, int castlingLine) {
        super(board);
        this.algos = List.of(
                new KingSideCastlingAlgo<>(board, castlingLine),
                new QueenSideCastlingAlgo<>(board, castlingLine)
        );
    }

    @Override
    public Collection<Castling> calculate(ROOK rook) {

        var castlings = Stream.of(board.getKing(rook.getColor()))
                .flatMap(Optional::stream)
                .map(king -> Pair.of(king, rook))
                .flatMap(pieces -> Stream.of(algos)
                        .flatMap(Collection::stream)
                        .map(algo -> algo.calculate(pieces))
                )
                .flatMap(Collection::stream)
                .toList();

        return castlings;
    }
}