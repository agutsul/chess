package com.agutsul.chess.rule.action;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.rule.AbstractRule;

public abstract class AbstractCastlingActionRule<COLOR  extends Color,
                                                 PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                                 PIECE2 extends Piece<COLOR> & Castlingable & Movable,
                                                 ACTION extends PieceCastlingAction<COLOR,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1,ACTION,Action.Type>
        implements CastlingActionRule<COLOR,PIECE1,PIECE2,ACTION> {

    protected final CastlingPieceAlgo<COLOR,PIECE1,Castling> algo;

    protected AbstractCastlingActionRule(Board board,
                                         CastlingPieceAlgo<COLOR,PIECE1,Castling> algo) {

        super(board, Action.Type.CASTLING);
        this.algo = algo;
    }

    protected PieceCastlingAction<COLOR,PIECE1,PIECE2>
            createAction(Castling castling, KingPiece<COLOR> king, RookPiece<COLOR> rook) {

        var kPosition = board.getPosition(castling.getKingTarget(), king.getPosition().y());
        var rPosition = board.getPosition(castling.getRookTarget(), rook.getPosition().y());

        @SuppressWarnings("unchecked")
        var action = new PieceCastlingAction<>(castling.side(),
                new CastlingMoveAction<>((PIECE1) king, kPosition.get()),
                new CastlingMoveAction<>((PIECE2) rook, rPosition.get())
        );

        return action;
    }
}