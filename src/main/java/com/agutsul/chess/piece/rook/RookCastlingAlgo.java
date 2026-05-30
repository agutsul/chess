package com.agutsul.chess.piece.rook;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.Castlingable.Castlings;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.AbstractCastlingAlgo;
import com.agutsul.chess.piece.algo.AbstractPositionAlgo;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.piece.algo.KingSideCastlingAlgo;
import com.agutsul.chess.piece.algo.QueenSideCastlingAlgo;
import com.agutsul.chess.position.Position;

final class RookCastlingAlgo<COLOR extends Color,
                             ROOK  extends RookPiece<COLOR>>
        extends AbstractPositionAlgo<ROOK,Castling>
        implements CastlingPieceAlgo<COLOR,ROOK,Castling> {

    private final Collection<AbstractCastlingAlgo<COLOR,KingPiece<COLOR>,ROOK>> algos;
    private final COLOR color;

    RookCastlingAlgo(Board board, COLOR color, int castlingLine) {
        super(board);
        this.color = color;
        this.algos = List.of(
                new KingSideCastlingAlgo<>(board,  color, castlingLine),
                new QueenSideCastlingAlgo<>(board, color, castlingLine)
        );
    }

    @Override
    public Collection<Castling> calculate(ROOK rook) {

        var castlings = Stream.of(board.getKing(rook.getColor()))
                .flatMap(Optional::stream)
                .flatMap(king -> Stream.of(algos)
                        .flatMap(Collection::stream)
                        .map(algo -> algo.calculate(Pair.of(king, rook)))
                )
                .flatMap(Collection::stream)
                .toList();

        return castlings;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Castling> calculate(Position position) {

        var rooks = Stream.of(board.getPieces(color, Piece.Type.ROOK))
                .flatMap(Collection::stream)
                .map(piece -> (ROOK) piece)
                .collect(toMap(Piece::getPosition, identity()));

        var castlings = Stream.of(board.getKing(color))
                .flatMap(Optional::stream)
                .flatMap(king -> Stream.of(algos)
                        .flatMap(Collection::stream)
                        .flatMap(algo -> Stream.ofNullable(Castlings.of(algo.getSide()))
                                .map(castling -> board.getPosition(castling.getRookSource(), algo.getCastlingLine()))
                                .flatMap(Optional::stream)
                                .filter(rookPosition -> Objects.equals(rookPosition, position))
                                .map(rookPosition -> rooks.get(rookPosition))
                                .map(Optional::ofNullable)
                                .flatMap(Optional::stream)
                                .map(rook -> algo.calculate(Pair.of(king, rook)))
                        )
                )
                .flatMap(Collection::stream)
                .toList();

        return castlings;
    }
}