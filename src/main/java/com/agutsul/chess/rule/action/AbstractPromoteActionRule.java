package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.action.AbstractTargetAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.Rule;

public abstract class AbstractPromoteActionRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                PAWN extends PawnPiece<COLOR1>,
                                                PIECE extends Piece<COLOR2> & Capturable,
                                                ACTION extends PiecePromoteAction<COLOR1, PAWN>,
                                                SOURCE_ACTION extends AbstractTargetAction<PAWN, ?>>
        extends AbstractRule<PAWN, ACTION>
        implements PromoteActionRule<COLOR1, PAWN, ACTION> {

    private final PromotePieceAlgo<COLOR1, PAWN, Position> algo;
    private final Rule<PAWN, Collection<SOURCE_ACTION>> rule;

    protected AbstractPromoteActionRule(Board board,
                                        PromotePieceAlgo<COLOR1, PAWN, Position> algo,
                                        Rule<PAWN, Collection<SOURCE_ACTION>> rule) {
        super(board);
        this.algo = algo;
        this.rule = rule;
    }

    @Override
    public final Collection<ACTION> evaluate(PAWN piece) {
        var nextPositions = algo.calculate(piece);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        var actions = new ArrayList<ACTION>();
        for (var action : rule.evaluate(piece)) {
            if (!nextPositions.contains(action.getPosition())) {
                continue;
            }

            actions.add(createAction(action));
        }

        return actions;
    }

    protected abstract ACTION createAction(SOURCE_ACTION sourceAction);
}