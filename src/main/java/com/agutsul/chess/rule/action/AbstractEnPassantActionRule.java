package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

public abstract class AbstractEnPassantActionRule<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  PAWN1 extends PawnPiece<COLOR1>,
                                                  PAWN2 extends PawnPiece<COLOR2>,
                                                  ACTION extends PieceEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2>>
        extends AbstractRule<PAWN1,ACTION,Action.Type>
        implements EnPassantActionRule<COLOR1,COLOR2,PAWN1,PAWN2,ACTION> {

    protected AbstractEnPassantActionRule(Board board) {
        super(board, Action.Type.EN_PASSANT);
    }

    @Override
    public Collection<ACTION> evaluate(PAWN1 pawn) {
        var nextPositions = calculatePositions(pawn);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        var actions = Stream.of(nextPositions)
                .flatMap(Collection::stream)
                .map(attackedPosition -> {
                    var optionalPawn = findOpponentPawn(pawn, attackedPosition);
                    if (optionalPawn.isEmpty()) {
                        return null;
                    }

                    var opponentPawn = optionalPawn.get();
                    var visitedPositions = opponentPawn.getPositions();
                    if (visitedPositions.size() < 2) {
                        return null;
                    }

                    var previousPosition = visitedPositions.get(visitedPositions.size() - 2);
                    var moveLength = Math.abs(previousPosition.y() - opponentPawn.getPosition().y());
                    // check if it was a big move for 2 positions
                    if (moveLength != PawnPiece.BIG_STEP_MOVE) {
                        return null;
                    }

                    return createAction(pawn, opponentPawn, attackedPosition);
                })
                .filter(Objects::nonNull)
                .toList();

        return actions;
    }

    protected abstract Collection<Position> calculatePositions(PAWN1 piece);

    protected abstract ACTION createAction(PAWN1 pawn1, PAWN2 pawn2, Position position);

    private Optional<PAWN2> findOpponentPawn(PAWN1 pawn, Position position) {
        @SuppressWarnings("unchecked")
        var optionalPawn = Stream.of(position)
                .filter(opponentPosition -> board.isEmpty(opponentPosition))
                .map(opponentPosition -> board.getPosition(opponentPosition.x(), pawn.getPosition().y()))
                .flatMap(Optional::stream)
                .map(opponentPosition -> board.getPiece(opponentPosition))
                .flatMap(Optional::stream)
                .filter(opponentPiece -> !Objects.equals(opponentPiece.getColor(), pawn.getColor()))
                .filter(Piece::isPawn)
                .map(opponentPiece -> (PAWN2) opponentPiece)
                .findFirst();

        return optionalPawn;
    }
}