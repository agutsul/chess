package com.agutsul.chess.rule.impact.deflection;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDeflectionAttackImpact;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceComparator;
import com.agutsul.chess.rule.impact.AbstractPieceImpactRule;
import com.agutsul.chess.rule.impact.DeflectionImpactRule;

// https://en.wikipedia.org/wiki/Deflection_(chess)
abstract class AbstractDeflectionImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            ATTACKER extends Piece<COLOR1> & Capturable,
                                            ATTACKED extends Piece<COLOR2>,
                                            DEFENDED extends Piece<COLOR2>,
                                            IMPACT extends PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractPieceImpactRule<COLOR1,ATTACKER,IMPACT>
        implements DeflectionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

    private static final Comparator<Piece<?>> COMPARATOR = new PieceComparator();

    AbstractDeflectionImpactRule(Board board) {
        super(board, Impact.Type.DEFLECTION);
    }

    protected Collection<IMPACT> createImpacts(AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(board.getImpacts(attackImpact.getTarget(), Impact.Type.PROTECT))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .map(PieceProtectImpact::getTarget)
                .map(protectedPiece -> (DEFENDED) protectedPiece)
                .filter(protectedPiece -> !board.getAttackers(protectedPiece).isEmpty())
                // protected piece should be more valuable than attacker piece
                .filter(protectedPiece -> COMPARATOR.compare(attackImpact.getSource(), protectedPiece) > 0)
                .filter(protectedPiece -> !confirmProtection(attackImpact, protectedPiece))
                .map(protectedPiece -> new PieceDeflectionAttackImpact<>(attackImpact, protectedPiece))
                .map(impact -> (IMPACT) impact)
                .toList();

        return impacts;
    }

    private boolean confirmProtection(AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact,
                                      DEFENDED protectedPiece) {
        // skip adding predator on board to simulate its capture by victim piece
        var tmpBoardBuilder = new PositionedBoardBuilder();
        Stream.of(board.getPieces())
            .flatMap(Collection::parallelStream)
            .filter(piece -> !Objects.equals(piece, attackImpact.getSource()))
            .filter(piece -> !Objects.equals(piece, attackImpact.getTarget()))
            .forEach(piece ->
                tmpBoardBuilder.withPiece(piece.getType(), piece.getColor(), piece.getPosition())
            );

        // locate victim on predator's position
        tmpBoardBuilder.withPiece(
                attackImpact.getTarget().getType(),
                attackImpact.getTarget().getColor(),
                attackImpact.getSource().getPosition()
        );

        var tmpBoard = tmpBoardBuilder.build();
        try {
            // check if protection from victim piece is still valid for protected piece
            var isProtected = Stream.of(tmpBoard.getPiece(attackImpact.getSource().getPosition()))
                    .flatMap(Optional::stream)
                    .map(piece -> tmpBoard.getImpacts(piece, Impact.Type.PROTECT))
                    .flatMap(Collection::parallelStream)
                    .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                    .map(PieceProtectImpact::getTarget)
                    .map(Piece::getPosition)
                    .anyMatch(position -> Objects.equals(position, protectedPiece.getPosition()));

            return isProtected;
        } finally {
            closeQuietly((Closeable) tmpBoard);
        }
    }
}