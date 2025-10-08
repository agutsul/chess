package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnEnPassantAlgo<COLOR extends Color,
                              PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN,Position>
        implements EnPassantPieceAlgo<COLOR,PAWN,Position> {

    private final CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo;

    PawnEnPassantAlgo(Board board,
                      CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo) {

        super(board);
        this.captureAlgo = captureAlgo;
    }

    @Override
    public Collection<Position> calculate(PAWN source) {
        var enPassantData = calculateData(source);
        return enPassantData.keySet();
    }

    Map<Position,PawnPiece<Color>> calculateData(PAWN pawn) {
        var nextPositions = captureAlgo.calculate(pawn);
        var data = Stream.of(nextPositions)
                .flatMap(Collection::stream)
                .map(attackedPosition ->
                    Stream.of(findOpponentPawn(pawn, attackedPosition))
                        .flatMap(Optional::stream)
                        .map(opponentPawn -> {
                            // check if it was a big move for 2 positions
                            var isBigStepActionExist = Stream.of(opponentPawn.getPositions())
                                    .filter(visitedPositions -> visitedPositions.size() >= 2)
                                    .map(visitedPositions -> visitedPositions.get(visitedPositions.size() - 2))
                                    .map(previousPosition -> Math.abs(previousPosition.y() - opponentPawn.getPosition().y()))
                                    .anyMatch(moveLength -> moveLength == PawnPiece.BIG_STEP_MOVE);

                            return Optional.ofNullable(isBigStepActionExist
                                    ? Pair.of(attackedPosition, opponentPawn)
                                    : null
                            );
                        })
                        .flatMap(Optional::stream)
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .collect(toMap(Pair::getLeft, Pair::getRight));

        return data;
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
}