package com.agutsul.chess.piece.king;

import static java.util.Collections.emptyList;
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

final class KingCastlingAlgo<COLOR extends Color,
                             KING  extends KingPiece<COLOR>>
        extends AbstractPositionAlgo<KING,Castling>
        implements CastlingPieceAlgo<COLOR,KING,Castling> {

    private final Collection<AbstractCastlingAlgo<COLOR,KING,RookPiece<COLOR>>> algos;
    private final COLOR color;

    KingCastlingAlgo(Board board, COLOR color, int castlingLine) {
        super(board);
        this.color = color;
        this.algos = List.of(
                new KingSideCastlingAlgo<>(board,  color, castlingLine),
                new QueenSideCastlingAlgo<>(board, color, castlingLine)
        );
    }

    @Override
    public Collection<Castling> calculate(KING king) {

        @SuppressWarnings("unchecked")
        var castlings = Stream.of(board.getPieces(king.getColor(), Piece.Type.ROOK))
                .flatMap(Collection::stream)
                .map(piece -> (RookPiece<COLOR>) piece)
                .flatMap(rook -> Stream.of(algos)
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
                .map(piece -> (RookPiece<COLOR>) piece)
                .collect(toMap(Piece::getPosition, identity()));

        if (rooks.isEmpty()) {
            return emptyList();
        }

        var castlings = Stream.of(board.getKing(color))
                .flatMap(Optional::stream)
                .map(piece -> (KING) piece)
                .filter(king -> Objects.equals(king.getPosition(), position))
                .flatMap(king -> Stream.of(algos)
                        .flatMap(Collection::stream)
                        .flatMap(algo -> Stream.ofNullable(Castlings.of(algo.getSide()))
                                .map(castling -> board.getPosition(castling.getRookSource(), algo.getCastlingLine()))
                                .flatMap(Optional::stream)
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