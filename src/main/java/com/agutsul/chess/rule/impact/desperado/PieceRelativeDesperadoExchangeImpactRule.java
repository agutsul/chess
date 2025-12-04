package com.agutsul.chess.rule.impact.desperado;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.DesperadoImpactRule;

public final class PieceRelativeDesperadoExchangeImpactRule<COLOR1 extends Color,
                                                            COLOR2 extends Color,
                                                            DESPERADO extends Piece<COLOR1> & Capturable,
                                                            ATTACKER extends Piece<COLOR2> & Capturable,
                                                            ATTACKED extends Piece<COLOR2>,
                                                            IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractRule<DESPERADO,IMPACT,Impact.Type>
        implements DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    public PieceRelativeDesperadoExchangeImpactRule(Board board) {
        super(board, Impact.Type.DESPERADO);
    }

    @Override
    public Collection<IMPACT> evaluate(DESPERADO piece) {
        var actions = findExchangeActions(piece);
        if (actions.isEmpty()) {
            return emptyList();
        }

        var opponentProtectImpacts = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::stream)
                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.PROTECT))
                .flatMap(Collection::stream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .collect(toList());

        @SuppressWarnings("unchecked")
        Collection<IMPACT> impacts = Stream.of(actions)
                .flatMap(Collection::stream)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(action -> createAttackImpact(
                        (DESPERADO) action.getSource(),
                        (ATTACKED)  action.getTarget()
                ))
                .flatMap(attackImpact -> {
                    var opponentProtects = Stream.of(opponentProtectImpacts)
                            .flatMap(Collection::stream)
                            .filter(protectImpact -> Objects.equals(protectImpact.getTarget(), attackImpact.getTarget()))
                            .collect(toList());

                    if (opponentProtects.isEmpty()) {
                        // unprotected piece
                        return Stream.of(new PieceDesperadoAttackImpact<>(
                                Mode.RELATIVE, attackImpact, null
                        ));
                    }

                    return Stream.of(opponentProtects)
                            .flatMap(Collection::stream)
                            .map(protectImpact -> createAttackImpact(
                                    (ATTACKER) protectImpact.getSource(),
                                    attackImpact.getSource(),
                                    protectImpact.getLine()
                            ))
                            .map(exchangeImpact -> new PieceDesperadoAttackImpact<>(
                                    Mode.RELATIVE, attackImpact, exchangeImpact
                            ));
                })
                .map(impact -> (IMPACT) impact)
                .distinct()
                .collect(toList());

        return impacts;
    }

    private Collection<AbstractCaptureAction<?,?,?,?>> findExchangeActions(Piece<?> piece) {
        Collection<AbstractCaptureAction<?,?,?,?>> actions = Stream.of(board.getPieces(piece.getColor()))
                .flatMap(Collection::stream)
                .filter(foundPiece -> !Objects.equals(piece, foundPiece))
                .flatMap(attacker -> Stream.of(board.getActions(attacker, Action.Type.CAPTURE))
                        .flatMap(Collection::stream)
                        .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                        // check if there is same or more valuable opponent piece under attack
                        .filter(action -> {
                            var attacked = action.getTarget();
                            return attacked.getType().rank() >= piece.getType().rank();
                        })
                )
                .sorted(comparing(
                        // sort most valuable attacked pieces first
                        AbstractCaptureAction::getTarget,
                        (piece1,piece2) -> Integer.compare(
                                piece2.getType().rank(),
                                piece1.getType().rank()
                        )
                    )
                )
                .collect(toList());

        return actions;
    }
}