package com.agutsul.chess.piece.king;

import static java.util.Collections.unmodifiableCollection;
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
import com.agutsul.chess.piece.algo.AbstractPositionAlgo;
import com.agutsul.chess.position.Position;

final class KingPieceAlgoProxy<COLOR extends Color,
                               KING  extends KingPiece<COLOR>>
        extends AbstractPositionAlgo<KING,Position>
        implements KingPieceAlgo<COLOR,KING> {

    enum Mode {
        MOVE,
        CAPTURE,
        DEFAULT
    }

    private final Mode mode;
    private final COLOR color;
    private final KingPieceAlgoImpl<COLOR,KING> algo;

    KingPieceAlgoProxy(Mode mode, Board board, COLOR color,
                       KingPieceAlgoImpl<COLOR,KING> algo) {

        super(board);

        this.mode = mode;
        this.color = color;
        this.algo = algo;
    }

    @Override
    public Collection<Position> calculate(KING piece) {
        var positions = algo.calculate(piece);
        return filterPositions(positions, color.invert());
    }

    @Override
    public Collection<Position> calculate(Position position) {
        var positions = algo.calculate(position);
        return filterPositions(positions, color.invert());
    }

    private Collection<Position> filterPositions(Collection<Position> positions,
                                                 Color opponentColor) {

        var filtered = switch (mode) {
        case MOVE -> movePositions(positions, opponentColor);
        case CAPTURE -> capturePositions(positions, opponentColor);
        default -> Stream.of(
                movePositions(positions, opponentColor),
                capturePositions(positions, opponentColor)
            )
            .flatMap(Collection::parallelStream)
            .distinct()
            .toList();
        };

        return unmodifiableCollection(filtered);
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