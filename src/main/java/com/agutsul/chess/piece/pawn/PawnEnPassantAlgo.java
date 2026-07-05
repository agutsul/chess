package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.color.Colors.isEqual;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractPositionAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnEnPassantAlgo<COLOR extends Color,
                              PAWN  extends PawnPiece<COLOR>>
        extends AbstractPositionAlgo<PAWN,EnPassant>
        implements EnPassantPieceAlgo<COLOR,PAWN,EnPassant> {

    private final PawnCaptureAlgo<COLOR,PAWN> captureAlgo;
    private final COLOR color;

    PawnEnPassantAlgo(Board board, COLOR color,
                      PawnCaptureAlgo<COLOR,PAWN> captureAlgo) {

        super(board);
        this.color = color;
        this.captureAlgo = captureAlgo;
    }

    @Override
    public Collection<EnPassant> calculate(Position attackerPosition) {
        var enPassants = Stream.of(captureAlgo.calculate(attackerPosition))
                .flatMap(Collection::stream)
                .flatMap(attackedPosition -> Stream.of(findOpponentPawn(attackerPosition, attackedPosition))
                        .flatMap(Optional::stream)
                        .map(opponentPawn -> createEnPassant(attackedPosition, opponentPawn))
                        .map(Optional::ofNullable)
                )
                .flatMap(Optional::stream)
                .toList();

        return enPassants;
    }

    private Optional<PawnPiece<Color>> findOpponentPawn(Position attackerPosition,
                                                        Position attackedPosition) {

        var optionalPawn = Stream.of(attackedPosition)
                .filter(opponentPosition -> board.isEmpty(opponentPosition))
                .map(opponentPosition -> board.getPosition(opponentPosition.x(), attackerPosition.y()))
                .flatMap(Optional::stream)
                .map(opponentPosition -> board.getPiece(opponentPosition))
                .flatMap(Optional::stream)
                .filter(opponentPiece -> !isEqual(opponentPiece.getColor(), this.color))
                .filter(Piece::isPawn)
                .map(piece -> (PawnPiece<Color>) piece)
                .findFirst();

        return optionalPawn;
    }

    private static EnPassant createEnPassant(Position attackedPosition, PawnPiece<?> opponentPawn) {
        // confirm only one action was performed and it was a big move
        return opponentPawn.getPositions().size() == 2 && opponentPawn.isBigMoved()
                ? new EnPassantImpl(attackedPosition, opponentPawn)
                : null;
    }

    private record EnPassantImpl(Position position, PawnPiece<?> piece) implements EnPassant {

        @Override
        public Position getPosition() {
            // attacked position
            return position();
        }

        @Override
        public PawnPiece<?> getPiece() {
            // attacked pawn piece
            return piece();
        }
    }
}