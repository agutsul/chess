package com.agutsul.chess.piece.pawn;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBlockadeImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.BlockadeImpactRule;

// https://en.wikipedia.org/wiki/Glossary_of_chess#Blockade
final class PawnBlockadeImpactRule<COLOR extends Color,
                                   PAWN extends PawnPiece<COLOR>,
                                   PIECE extends Piece<Color>>
        extends AbstractRule<PAWN,PieceBlockadeImpact<COLOR,PAWN,PIECE>,Impact.Type>
        implements BlockadeImpactRule<COLOR,PAWN,PIECE,PieceBlockadeImpact<COLOR,PAWN,PIECE>> {

    private final int promotionLine;

    public PawnBlockadeImpactRule(Board board, int promotionLine) {
        super(board, Impact.Type.BLOCKADE);
        this.promotionLine = promotionLine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<PieceBlockadeImpact<COLOR,PAWN,PIECE>> evaluate(PAWN pawn) {
        var pawnPosition = pawn.getPosition();

        var possibleMovePositions = IntStream.rangeClosed(
                    min(pawnPosition.y(), promotionLine),
                    max(pawnPosition.y(), promotionLine)
                )
                .mapToObj(y -> board.getPosition(pawnPosition.x(), y))
                .flatMap(Optional::stream)
                .toList();

        var sameColorBlockImpacts = Stream.of(board.getPieces(pawn.getColor()))
                .flatMap(Collection::stream)
                .filter(piece -> !Objects.equals(piece, pawn))
                .filter(piece -> possibleMovePositions.contains(piece.getPosition()))
                .map(piece -> new PieceBlockadeImpact<>(pawn, (PIECE) piece))
                .toList();

        var attackerBlockImpacts = Stream.of(board.getPieces(pawn.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(piece -> {
                    var isBlocked = possibleMovePositions.contains(piece.getPosition());
                    if (!isBlocked) {
                        return Stream.of(piece.getImpacts(Impact.Type.CONTROL))
                                .flatMap(Collection::stream)
                                .map(Impact::getPosition)
                                .anyMatch(position -> possibleMovePositions.contains(position));
                    }

                    return isBlocked;
                })
                .map(piece -> new PieceBlockadeImpact<>(pawn, (PIECE) piece))
                .toList();

        var impacts = new ArrayList<PieceBlockadeImpact<COLOR,PAWN,PIECE>>();

        impacts.addAll(sameColorBlockImpacts);
        impacts.addAll(attackerBlockImpacts);

        return impacts;
    }
}