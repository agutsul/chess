package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.IntStream;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceStagnantImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.StagnantImpactRule;

final class PawnStagnantImpactRule<COLOR extends Color,
                                   PAWN extends PawnPiece<COLOR>,
                                   PIECE extends Piece<Color>>
        extends AbstractRule<PAWN,PieceStagnantImpact<COLOR,PAWN,PIECE>,Impact.Type>
        implements StagnantImpactRule<COLOR,PAWN,PIECE,PieceStagnantImpact<COLOR,PAWN,PIECE>> {

    private final int promotionLine;

    public PawnStagnantImpactRule(Board board, int promotionLine) {
        super(board, Impact.Type.STAGNANT);
        this.promotionLine = promotionLine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<PieceStagnantImpact<COLOR,PAWN,PIECE>> evaluate(PAWN pawn) {
        var pawnPosition = pawn.getPosition();

        var possibleMovePositions = IntStream.rangeClosed(
                    min(pawnPosition.y(), promotionLine),
                    max(pawnPosition.y(), promotionLine)
                )
                .mapToObj(y -> positionOf(pawnPosition.x(), y))
                .toList();

        var sameColorBlockImpacts = board.getPieces(pawn.getColor()).stream()
                .filter(piece -> !Objects.equals(piece, pawn))
                .filter(piece -> possibleMovePositions.contains(piece.getPosition()))
                .map(piece -> new PieceStagnantImpact<>(pawn, (PIECE) piece))
                .toList();

        var attackerBlockImpacts = board.getPieces(pawn.getColor().invert()).stream()
                .filter(piece -> {
                    var isBlocked = possibleMovePositions.contains(piece.getPosition());
                    if (!isBlocked) {
                        var controlImpacts = piece.getImpacts(Impact.Type.CONTROL);
                        return controlImpacts.stream()
                                .map(Impact::getPosition)
                                .anyMatch(position -> possibleMovePositions.contains(position));
                    }

                    return isBlocked;
                })
                .map(piece -> new PieceStagnantImpact<>(pawn, (PIECE) piece))
                .toList();

        var impacts = new ArrayList<PieceStagnantImpact<COLOR,PAWN,PIECE>>();

        impacts.addAll(sameColorBlockImpacts);
        impacts.addAll(attackerBlockImpacts);

        return impacts;
    }
}