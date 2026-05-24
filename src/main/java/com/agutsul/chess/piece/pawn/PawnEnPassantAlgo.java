package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnEnPassantAlgo<COLOR extends Color,
                              PAWN  extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN,EnPassant>
        implements EnPassantPieceAlgo<COLOR,PAWN,EnPassant> {

    private final CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo;

    PawnEnPassantAlgo(Board board,
                      CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo) {

        super(board);
        this.captureAlgo = captureAlgo;
    }

    @Override
    public Collection<EnPassant> calculate(PAWN attacker) {
        Collection<EnPassant> enPassants = Stream.of(captureAlgo.calculate(attacker))
                .flatMap(Collection::stream)
                .flatMap(attackedPosition -> Stream.of(findOpponentPawn(attacker, attackedPosition))
                        .flatMap(Optional::stream)
                        .map(opponentPawn -> containsBigMoveAction(opponentPawn)
                                    ? new EnPassantImpl(attackedPosition, opponentPawn)
                                    : null
                        )
                        .map(Optional::ofNullable)
                )
                .flatMap(Optional::stream)
                .collect(toList());

        return enPassants;
    }

    private Optional<PawnPiece<Color>> findOpponentPawn(PAWN pawn, Position position) {
        var optionalPawn = Stream.of(position)
                .filter(opponentPosition -> board.isEmpty(opponentPosition))
                .map(opponentPosition -> board.getPosition(opponentPosition.x(), pawn.getPosition().y()))
                .flatMap(Optional::stream)
                .map(opponentPosition -> board.getPiece(opponentPosition))
                .flatMap(Optional::stream)
                .filter(opponentPiece -> !Objects.equals(opponentPiece.getColor(), pawn.getColor()))
                .filter(Piece::isPawn)
                .map(piece -> (PawnPiece<Color>) piece)
                .findFirst();

        return optionalPawn;
    }

    // check if there was a big move for 2 positions in opponent piece's history
    private static boolean containsBigMoveAction(PawnPiece<Color> opponentPawn) {
        return Stream.of(opponentPawn.getPositions())
                // confirm only one action was performed and it was big move
                .anyMatch(positions -> positions.size() == 2 && opponentPawn.isBigMoved());
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