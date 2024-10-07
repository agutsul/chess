package com.agutsul.chess.piece.pawn;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractPromoteActionRule;
import com.agutsul.chess.rule.action.CaptureActionRule;
import com.agutsul.chess.rule.action.MoveActionRule;
import com.agutsul.chess.rule.action.PromoteActionRule;

final class PawnPromoteActionRule<C extends Color,
                                  P extends PawnPiece<C>>
        implements PromoteActionRule<C, P, PiecePromoteAction<C, P>> {

    private final PromoteActionRule<C, P, PiecePromoteAction<C, P>> promoteActionRuleAdapter;

    <C2 extends Color, P2 extends Piece<C2> & Capturable> PawnPromoteActionRule(
                                 Board board,
                                 PromotePieceAlgo<C, P, Position> algo,
                                 MoveActionRule<C, P, PieceMoveAction<C, P>> rule) {

        this.promoteActionRuleAdapter =
                new PawnPromoteMoveActionRule<C, C2, P, P2>(board, algo, rule);
    }

    <C2 extends Color, P2 extends Piece<C2> & Capturable> PawnPromoteActionRule(
                                 Board board,
                                 PromotePieceAlgo<C, P, Position> algo,
                                 CaptureActionRule<C, C2, P, P2, PieceCaptureAction<C,C2,P,P2>> rule) {

        this.promoteActionRuleAdapter =
                new PawnPromoteCaptureActionRule<C, C2, P, P2>(board, algo, rule);
    }

    @Override
    public Collection<PiecePromoteAction<C, P>> evaluate(P pawn) {
        return promoteActionRuleAdapter.evaluate(pawn);
    }

    // Adapters

    private static final class PawnPromoteCaptureActionRule<C1 extends Color,
                                                            C2 extends Color,
                                                            PAWN extends PawnPiece<C1>,
                                                            PIECE extends Piece<C2> & Capturable>
            extends AbstractPromoteActionRule<C1,
                                              C2,
                                              PAWN,
                                              PIECE,
                                              PiecePromoteAction<C1, PAWN>,
                                              PieceCaptureAction<C1, C2, PAWN, PIECE>> {

        PawnPromoteCaptureActionRule(Board board,
                                     PromotePieceAlgo<C1, PAWN, Position> algo,
                                     CaptureActionRule<C1, C2, PAWN, PIECE, PieceCaptureAction<C1, C2, PAWN, PIECE>> rule) {
            super(board, algo, rule);
        }

        @Override
        protected PiecePromoteAction<C1, PAWN> createAction(PieceCaptureAction<C1, C2, PAWN, PIECE> sourceAction) {
            return new PiecePromoteAction<C1, PAWN>(board, sourceAction);
        }
    }

    private static final class PawnPromoteMoveActionRule<C1 extends Color,
                                                         C2 extends Color,
                                                         PAWN extends PawnPiece<C1>,
                                                         PIECE extends Piece<C2> & Capturable>
            extends AbstractPromoteActionRule<C1,
                                              C2,
                                              PAWN,
                                              PIECE,
                                              PiecePromoteAction<C1, PAWN>,
                                              PieceMoveAction<C1, PAWN>> {

        PawnPromoteMoveActionRule(Board board,
                                  PromotePieceAlgo<C1, PAWN, Position> algo,
                                  MoveActionRule<C1, PAWN, PieceMoveAction<C1, PAWN>> rule) {
            super(board, algo, rule);
        }

        @Override
        protected PiecePromoteAction<C1, PAWN> createAction(PieceMoveAction<C1, PAWN> sourceAction) {
            return new PiecePromoteAction<C1, PAWN>(board, sourceAction);
        }
    }
}