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
import com.agutsul.chess.position.Position;

final class KingPieceAlgoProxy<COLOR extends Color,
                               PIECE extends KingPiece<COLOR>>
        extends AbstractAlgo<PIECE,Position>
        implements KingPieceAlgo<COLOR,PIECE> {

    enum Mode {
        MOVE,
        CAPTURE,
        DEFAULT
    }

    private final Mode mode;
    private final KingPieceAlgo<COLOR,PIECE> algo;

    KingPieceAlgoProxy(Mode mode, Board board,
                       KingPieceAlgo<COLOR,PIECE> algo) {

        super(board);

        this.mode = mode;
        this.algo = algo;
    }

    @Override
    public Collection<Position> calculate(PIECE piece) {
        var opponentColor = piece.getColor().invert();

        var calculatedPositions = algo.calculate(piece);

        var positions = switch (mode) {
        case MOVE -> movePositions(calculatedPositions, opponentColor);
        case CAPTURE -> capturePositions(calculatedPositions, opponentColor);
        default -> Stream.of(
                movePositions(calculatedPositions, opponentColor),
                capturePositions(calculatedPositions, opponentColor)
            )
            .flatMap(Collection::parallelStream)
            .distinct()
            .toList();
        };

        return positions;
    }

    private Collection<Position> movePositions(Collection<Position> positions,
                                               Color opponentColor) {

        var filtered = Stream.of(positions)
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .filter(position -> !board.isAttacked(position,  opponentColor))
                .filter(position -> !board.isMonitored(position, opponentColor))
                .toList();

        return filtered;
    }

    private Collection<Position> capturePositions(Collection<Position> positions,
                                                  Color opponentColor) {

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