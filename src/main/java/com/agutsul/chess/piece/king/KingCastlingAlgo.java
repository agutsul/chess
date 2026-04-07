package com.agutsul.chess.piece.king;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Castlingable.Castlings;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.AbstractCastlingAlgo;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.piece.algo.KingSideCastlingAlgo;
import com.agutsul.chess.piece.algo.QueenSideCastlingAlgo;

final class KingCastlingAlgo<COLOR extends Color,
                             KING  extends KingPiece<COLOR>>
        extends AbstractAlgo<KING,Castlings>
        implements CastlingPieceAlgo<COLOR,KING,Castlings> {

    private final Collection<AbstractCastlingAlgo<COLOR,KING,RookPiece<COLOR>>> algos;

    KingCastlingAlgo(Board board) {
        super(board);
        this.algos = List.of(new KingSideCastlingAlgo<>(board), new QueenSideCastlingAlgo<>(board));
    }

    @Override
    public Collection<Castlings> calculate(KING king) {

        @SuppressWarnings("unchecked")
        var castlings = Stream.of(board.getPieces(king.getColor(), Piece.Type.ROOK))
                .flatMap(Collection::stream)
                .map(rook -> Pair.of(king, (RookPiece<COLOR>) rook))
                .flatMap(pieces -> Stream.of(algos)
                        .flatMap(Collection::stream)
                        .map(algo -> algo.calculate(pieces))
                )
                .flatMap(Collection::stream)
                .toList();

        return castlings;
    }
}