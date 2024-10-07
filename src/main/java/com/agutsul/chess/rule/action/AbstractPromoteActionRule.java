package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.AbstractTargetAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.Rule;

public abstract class AbstractPromoteActionRule<C1 extends Color,
                                                C2 extends Color,
                                                P1 extends PawnPiece<C1>,
                                                P2 extends Piece<C2> & Capturable,
                                                A extends PiecePromoteAction<C1, P1>,
                                                SA extends AbstractTargetAction<P1, ?>>
        extends AbstractRule<P1, A>
        implements PromoteActionRule<C1, P1, A> {

    private final PromotePieceAlgo<C1, P1, Position> algo;
    private final Rule<P1, Collection<SA>> rule;

    protected AbstractPromoteActionRule(Board board,
                                        PromotePieceAlgo<C1, P1, Position> algo,
                                        Rule<P1, Collection<SA>> rule) {
        super(board);
        this.algo = algo;
        this.rule = rule;
    }

    @Override
    public final Collection<A> evaluate(P1 piece) {
        var nextPositions = algo.calculate(piece);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        var actions = new ArrayList<A>();
        for (var action : rule.evaluate(piece)) {
            if (!nextPositions.contains(action.getPosition())) {
                continue;
            }

            actions.add(createAction(action));
        }

        return actions;
    }

    protected abstract A createAction(SA sourceAction);
}
