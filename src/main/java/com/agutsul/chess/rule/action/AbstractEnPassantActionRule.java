package com.agutsul.chess.rule.action;

import static com.agutsul.chess.piece.Piece.isPawn;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
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

        var actions = new ArrayList<ACTION>();
        for (var attackedPosition : nextPositions) {
            if (!board.isEmpty(attackedPosition)) {
                continue;
            }

            var enemyPiecePosition =
                    board.getPosition(attackedPosition.x(), pawn.getPosition().y());

            if (enemyPiecePosition.isEmpty()) {
                continue;
            }

            var optionalEnemyPiece = board.getPiece(enemyPiecePosition.get());
            if (optionalEnemyPiece.isEmpty()) {
                continue;
            }

            var enemyPiece = optionalEnemyPiece.get();
            if (Objects.equals(enemyPiece.getColor(), pawn.getColor()) || !isPawn(enemyPiece)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var enemyPawn = (PAWN2) enemyPiece;

            var visitedPositions = enemyPawn.getPositions();
            if (visitedPositions.size() < 2) {
                continue;
            }

            var previousPosition = visitedPositions.get(visitedPositions.size() - 2);
            var moveLength = Math.abs(previousPosition.y() - enemyPawn.getPosition().y());
            // check if it was a big move for 2 positions
            if (moveLength != PawnPiece.BIG_STEP_MOVE) {
                continue;
            }

            actions.add(createAction(pawn, enemyPawn, attackedPosition));
        }

        return actions;
    }

    protected abstract Collection<Position> calculatePositions(PAWN1 piece);

    protected abstract ACTION createAction(PAWN1 pawn1, PAWN2 pawn2, Position position);
}
