package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
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
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Deflection_(chess)
abstract class AbstractDeflectionImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            ATTACKER extends Piece<COLOR1> & Capturable,
                                            ATTACKED extends Piece<COLOR2>,
                                            DEFENDED extends Piece<COLOR2>,
                                            IMPACT extends PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements DeflectionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

    AbstractDeflectionImpactRule(Board board) {
        super(board, Impact.Type.DEFLECTION);
    }

    @Override
    public final Collection<IMPACT> evaluate(ATTACKER piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(ATTACKER piece);

    protected abstract Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next);

    protected Collection<IMPACT> createImpacts(AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(board.getImpacts(attackImpact.getTarget(), Impact.Type.PROTECT))
                .flatMap(Collection::stream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .map(PieceProtectImpact::getTarget)
                .map(protectedPiece -> (DEFENDED) protectedPiece)
                .filter(protectedPiece -> !board.getAttackers(protectedPiece).isEmpty())
                // protected piece should be more valuable than attacker piece
                .filter(protectedPiece -> protectedPiece.getType().rank() > attackImpact.getSource().getType().rank())
                .filter(protectedPiece -> !confirmProtection(attackImpact, protectedPiece))
                .map(protectedPiece -> new PieceDeflectionAttackImpact<>(attackImpact, protectedPiece))
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }

    private boolean confirmProtection(AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact,
                                      DEFENDED protectedPiece) {
        // skip adding predator on board to simulate its capture by victim piece
        var tmpBoardBuilder = new PositionedBoardBuilder();
        Stream.of(board.getPieces())
            .flatMap(Collection::stream)
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
                    .anyMatch(piece -> Stream.of(tmpBoard.getImpacts(piece, Impact.Type.PROTECT))
                            .flatMap(Collection::stream)
                            .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                            .map(PieceProtectImpact::getTarget)
                            .map(Piece::getPosition)
                            .anyMatch(position -> Objects.equals(position, protectedPiece.getPosition()))
                    );

            return isProtected;
        } finally {
            closeQuietly((Closeable) tmpBoard);
        }
    }
}