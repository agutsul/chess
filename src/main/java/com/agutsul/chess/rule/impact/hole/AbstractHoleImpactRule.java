package com.agutsul.chess.rule.impact.hole;

import static com.agutsul.chess.piece.Piece.isKing;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PositionHoleImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.HoleImpactRule;

// https://en.wikipedia.org/wiki/Glossary_of_chess#holes
abstract class AbstractHoleImpactRule<POSITION extends Position,
                                      IMPACT   extends PositionHoleImpact<POSITION>>
        extends AbstractRule<POSITION,IMPACT,Impact.Type>
        implements HoleImpactRule<POSITION,IMPACT> {

    private final Color color;

    AbstractHoleImpactRule(Board board, Color color) {
        super(board, Impact.Type.HOLE);
        this.color = color;
    }

    @Override
    public Collection<IMPACT> evaluate(POSITION position) {
        if (board.getPiece(position).isPresent()) {
            return emptyList();
        }

        var isPawnFound = Stream.of(
                    positionOf(position.x() + 1, position.y()),     // right to position
                    positionOf(position.x() - 1, position.y())      // left  to position
                )
                .filter(Objects::nonNull)
                .map(piecePosition -> board.getPiece(piecePosition))
                .flatMap(Optional::stream)
                .filter(Piece::isPawn)
                .map(piece -> (PawnPiece<?>) piece)
                // skip calculating holes:
                // - when it is different color
                // - when pawn is far away it's initial position
                .anyMatch(pawn -> Objects.equals(pawn.getColor(), color)
                        && pawn.isMoved() && pawn.getPositions().size() <= 3
                );

        if (!isPawnFound) {
            return emptyList();
        }

        var isControlledPosition = Stream.of(board.getPieces(color))
                .flatMap(Collection::parallelStream)
                .filter(piece -> isKing(piece) || !((Pinnable) piece).isPinned())
                .map(piece -> board.getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::parallelStream)
                .map(Impact::getPosition)
                .anyMatch(controlledPosition -> Objects.equals(controlledPosition, position));

        // if position is under control => it is not a hole
        if (isControlledPosition) {
            return emptyList();
        }

        var impacts = Stream.of(board.getKing(color))
                .flatMap(Optional::stream)
                .filter(kingPiece -> impactExists(kingPiece, position))
                .map(kingPiece -> createImpact(color, position))
                .toList();

        return impacts;
    }

    abstract boolean impactExists(KingPiece<?> kingPiece, POSITION position);

    abstract IMPACT createImpact(Color color, POSITION position);
}