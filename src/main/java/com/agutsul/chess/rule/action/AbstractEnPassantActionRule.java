package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

public abstract class AbstractEnPassantActionRule<C1 extends Color,
                                                  C2 extends Color,
                                                  P1 extends PawnPiece<C1>,
                                                  P2 extends PawnPiece<C2>,
                                                  A extends PieceEnPassantAction<C1, C2, P1, P2>>
        extends AbstractRule<P1, A>
        implements EnPassantActionRule<C1, C2, P1, P2, A> {

    protected AbstractEnPassantActionRule(Board board) {
        super(board);
    }

    @Override
    public Collection<A> evaluate(P1 pawn) {
        var nextPositions = calculatePositions(pawn);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        var actions = new ArrayList<A>();
        for (var attackedPosition : nextPositions) {
            var enemyPiecePosition = board.getPosition(attackedPosition.x(), pawn.getPosition().y());
            if (enemyPiecePosition.isEmpty()) {
                continue;
            }

            var optionalEnemyPiece = board.getPiece(enemyPiecePosition.get());
            if (optionalEnemyPiece.isEmpty()) {
                continue;
            }

            var enemyPiece = optionalEnemyPiece.get();
            if (Objects.equals(enemyPiece.getColor(), pawn.getColor())
                    || !Piece.Type.PAWN.equals(enemyPiece.getType())) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var enemyPawn = (P2) enemyPiece;

            var visitedPositions = enemyPawn.getPositions();
            if (visitedPositions.size() < 2) {
                continue;
            }

            var previousPosition = visitedPositions.get(visitedPositions.size() - 2);
            var moveLength = Math.abs(previousPosition.y() - enemyPawn.getPosition().y());
            // check if it was a big move for 2 positions
            if (moveLength != 2) {
                continue;
            }

            actions.add(createAction(pawn, enemyPawn, attackedPosition));
        }

        return actions;
    }

    protected abstract Collection<Position> calculatePositions(P1 piece);

    protected abstract A createAction(P1 piece1, P2 piece2, Position position);
}
