package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.IntStream;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.BlockImpactRule;

class PawnBlockImpactRule<COLOR extends Color,
                          PAWN extends PawnPiece<COLOR>,
                          PIECE extends Piece<Color>>
        extends AbstractRule<PAWN,PieceBlockImpact<COLOR,PAWN,PIECE>,Impact.Type>
        implements BlockImpactRule<COLOR,PAWN,PIECE,PieceBlockImpact<COLOR,PAWN,PIECE>> {

    private final int promotionLine;

    public PawnBlockImpactRule(Board board, int promotionLine) {
        super(board, Impact.Type.BLOCK);
        this.promotionLine = promotionLine;
    }

    @Override
    public Collection<PieceBlockImpact<COLOR,PAWN,PIECE>> evaluate(PAWN pawn) {
        var pawnPosition = pawn.getPosition();

        var possibleMovePositions = IntStream.rangeClosed(
                    min(pawnPosition.y(), promotionLine),
                    max(pawnPosition.y(), promotionLine)
                )
                .mapToObj(y -> positionOf(pawnPosition.x(), y))
                .toList();

        @SuppressWarnings("unchecked")
        var sameColorBlockImpacts = board.getPieces(pawn.getColor()).stream()
                .filter(piece -> !Objects.equals(piece, pawn))
                .filter(piece -> possibleMovePositions.contains(piece.getPosition()))
                .map(piece -> new PieceBlockImpact<>(pawn, (PIECE) piece))
                .toList();

        @SuppressWarnings("unchecked")
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
                .map(piece -> new PieceBlockImpact<>(pawn, (PIECE) piece))
                .toList();

        var impacts = new ArrayList<PieceBlockImpact<COLOR,PAWN,PIECE>>();

        impacts.addAll(sameColorBlockImpacts);
        impacts.addAll(attackerBlockImpacts);

        return impacts;
    }
}