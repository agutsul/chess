package com.agutsul.chess.piece.pawn;

import java.util.Collection;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.action.AbstractPromoteActionRule;
import com.agutsul.chess.rule.action.CaptureActionRule;
import com.agutsul.chess.rule.action.MoveActionRule;
import com.agutsul.chess.rule.action.PromoteActionRule;

final class PawnPromoteActionRule<COLOR1 extends Color,
                                  PAWN extends PawnPiece<COLOR1>>
        extends AbstractRule<PAWN,PiecePromoteAction<COLOR1,PAWN>,Action.Type>
        implements PromoteActionRule<COLOR1,PAWN,PiecePromoteAction<COLOR1,PAWN>> {

    private final PromoteActionRule<COLOR1,PAWN,PiecePromoteAction<COLOR1,PAWN>> promoteActionRuleAdapter;

    <COLOR2 extends Color,PIECE extends Piece<COLOR2>> PawnPromoteActionRule(
                                 Board board,
                                 PromotePieceAlgo<COLOR1,PAWN, Position> algo,
                                 MoveActionRule<COLOR1,PAWN,PieceMoveAction<COLOR1,PAWN>> rule) {

        super(board, Action.Type.PROMOTE);
        this.promoteActionRuleAdapter = new PawnPromoteMoveActionRule<>(board, algo, rule);
    }

    <COLOR2 extends Color,PIECE extends Piece<COLOR2>> PawnPromoteActionRule(
                                 Board board,
                                 PromotePieceAlgo<COLOR1,PAWN,Position> algo,
                                 CaptureActionRule<COLOR1,COLOR2,PAWN,PIECE,
                                                   PieceCaptureAction<COLOR1,COLOR2,PAWN,PIECE>> rule) {

        super(board, Action.Type.PROMOTE);
        this.promoteActionRuleAdapter = new PawnPromoteCaptureActionRule<>(board, algo, rule);
    }

    @Override
    public Collection<PiecePromoteAction<COLOR1,PAWN>> evaluate(PAWN pawn) {
        return promoteActionRuleAdapter.evaluate(pawn);
    }

    // Adapters

    private static final class PawnPromoteCaptureActionRule<COLOR1 extends Color,
                                                            COLOR2 extends Color,
                                                            PAWN extends PawnPiece<COLOR1>,
                                                            PIECE extends Piece<COLOR2>>
            extends AbstractPromoteActionRule<COLOR1,
                                              COLOR2,
                                              PAWN,
                                              PIECE,
                                              PiecePromoteAction<COLOR1,PAWN>,
                                              PieceCaptureAction<COLOR1,COLOR2,PAWN,PIECE>> {

        PawnPromoteCaptureActionRule(Board board,
                                     PromotePieceAlgo<COLOR1,PAWN,Position> algo,
                                     CaptureActionRule<COLOR1,COLOR2,PAWN,PIECE,
                                                       PieceCaptureAction<COLOR1,COLOR2,PAWN,PIECE>> rule) {
            super(board, algo, rule);
        }

        @Override
        protected PiecePromoteAction<COLOR1,PAWN> createAction(
                                     PieceCaptureAction<COLOR1,COLOR2,PAWN,PIECE> sourceAction) {

            return new PiecePromoteAction<>(sourceAction, (Observable) board);
        }
    }

    private static final class PawnPromoteMoveActionRule<COLOR1 extends Color,
                                                         COLOR2 extends Color,
                                                         PAWN extends PawnPiece<COLOR1>,
                                                         PIECE extends Piece<COLOR2>>
            extends AbstractPromoteActionRule<COLOR1,
                                              COLOR2,
                                              PAWN,
                                              PIECE,
                                              PiecePromoteAction<COLOR1,PAWN>,
                                              PieceMoveAction<COLOR1,PAWN>> {

        PawnPromoteMoveActionRule(Board board,
                                  PromotePieceAlgo<COLOR1,PAWN,Position> algo,
                                  MoveActionRule<COLOR1,PAWN,
                                                 PieceMoveAction<COLOR1,PAWN>> rule) {
            super(board, algo, rule);
        }

        @Override
        protected PiecePromoteAction<COLOR1,PAWN> createAction(
                                     PieceMoveAction<COLOR1,PAWN> sourceAction) {

            return new PiecePromoteAction<>(sourceAction, (Observable) board);
        }
    }
}