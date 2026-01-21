package com.agutsul.chess.piece.king;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

final class KingPieceAlgoAdapter<COLOR extends Color,
                                 PIECE extends KingPiece<COLOR>>
        extends AbstractAlgo<PIECE,Position>
        implements MovePieceAlgo<COLOR,PIECE,Position>,
                   CapturePieceAlgo<COLOR,PIECE,Position> {

    private final Algo<PIECE,Collection<Position>> algo;

    KingPieceAlgoAdapter(Board board,
                         Algo<PIECE,Collection<Position>> algo) {

        super(board);
        this.algo = algo;
    }

    @Override
    public Collection<Position> calculate(PIECE piece) {
        var opponentColor = piece.getColor().invert();

        var calculatedPositions = algo.calculate(piece);
        var positions = Stream.of(
                    filterMovePositions(calculatedPositions, opponentColor),
                    filterCapturePositions(calculatedPositions, opponentColor)
                )
                .flatMap(Collection::parallelStream)
                .distinct()
                .toList();

        return positions;
    }

    private Collection<Position> filterMovePositions(Collection<Position> positions, Color opponentColor) {
        var filtered = Stream.of(positions)
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .filter(position -> !board.isAttacked(position,  opponentColor))
                .filter(position -> !board.isMonitored(position, opponentColor))
                .toList();

        return filtered;
    }

    private Collection<Position> filterCapturePositions(Collection<Position> positions, Color opponentColor) {
        var filtered = Stream.of(positions)
                .flatMap(Collection::stream)
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> Objects.equals(foundPiece.getColor(), opponentColor))
                .filter(not(Piece::isKing))
                .filter(opponentPiece -> !((Protectable) opponentPiece).isProtected())
                .filter(opponentPiece -> !board.isMonitored(opponentPiece.getPosition(), opponentColor))
                .map(Piece::getPosition)
                .toList();

        return filtered;
    }
}