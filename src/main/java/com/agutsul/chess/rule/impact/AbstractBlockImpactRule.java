package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static com.agutsul.chess.rule.impact.LineImpactRule.LINE_ATTACK_PIECE_TYPES;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceBlockAttackImpact;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Block_(chess)
abstract class AbstractBlockImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       BLOCKER extends Piece<COLOR1>& Movable,
                                       ATTACKED extends Piece<COLOR1>,
                                       ATTACKER extends Piece<COLOR2> & Capturable,
                                       IMPACT extends PieceBlockImpact<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER>>
        extends AbstractRule<BLOCKER,IMPACT,Impact.Type>
        implements BlockImpactRule<COLOR1,COLOR2,BLOCKER,ATTACKED,ATTACKER,IMPACT> {

    AbstractBlockImpactRule(Board board) {
        super(board, Impact.Type.BLOCK);
    }

    @Override
    public final Collection<IMPACT> evaluate(BLOCKER piece) {
        var nextPositions = calculate(piece);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, nextPositions);
    }

    protected abstract Collection<Calculated> calculate(BLOCKER piece);

    @SuppressWarnings("unchecked")
    protected Collection<IMPACT> createImpacts(BLOCKER piece, Collection<Calculated> next) {
        var piecePositions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .collect(toSet());

        var impacts = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(opponentPiece -> LINE_ATTACK_PIECE_TYPES.contains(opponentPiece.getType()))
                .map(opponentPiece -> board.getActions(opponentPiece, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .map(action -> (PieceCaptureAction<COLOR2,COLOR1,ATTACKER,ATTACKED>) action)
                .flatMap(action -> Stream.of(action.getLine())
                        .flatMap(Optional::stream)
                        .filter(attackLine -> attackLine.containsAny(piecePositions))
                        .flatMap(attackLine -> Stream.of(attackLine.intersection(piecePositions))
                                .flatMap(Collection::stream)
                                .filter(blockPosition -> board.isEmpty(blockPosition))
                                .map(blockPosition -> new PieceBlockAttackImpact<>(
                                        piece, blockPosition, createAttackImpact(action)
                                ))
                        )
                )
                .sorted(comparing(
                        PieceBlockAttackImpact::getAttacked, // sort most valuable defended pieces first
                        (piece1,piece2) -> Integer.compare(
                                piece2.getType().rank(),
                                piece1.getType().rank()
                        )
                    )
                )
                .map(impact -> (IMPACT) impact)
                .distinct()
                .collect(toList());

        return impacts;
    }

    @SuppressWarnings("unchecked")
    private AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,ATTACKED>
            createAttackImpact(PieceCaptureAction<COLOR2,COLOR1,ATTACKER,ATTACKED> action) {

        var line = action.getLine().get();
        var attackImpact = isKing(action.getTarget())
                ? new PieceCheckImpact<>(action.getSource(), (KingPiece<COLOR1>) action.getTarget(), line)
                : new PieceAttackImpact<>(action.getSource(), action.getTarget(), line);

        return (AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,ATTACKED>) attackImpact;
    }
}