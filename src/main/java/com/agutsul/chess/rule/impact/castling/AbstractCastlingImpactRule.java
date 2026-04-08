package com.agutsul.chess.rule.impact.castling;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCastlingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.CastlingImpactRule;

public abstract class AbstractCastlingImpactRule<COLOR  extends Color,
                                                 PIECE1 extends Piece<COLOR> & Movable & Castlingable,
                                                 PIECE2 extends Piece<COLOR> & Movable & Castlingable,
                                                 IMPACT extends PieceCastlingImpact<COLOR,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1,IMPACT,Impact.Type>
        implements CastlingImpactRule<COLOR,PIECE1,PIECE2,IMPACT> {

    protected final CastlingPieceAlgo<COLOR,PIECE1,Castling> algo;

    protected AbstractCastlingImpactRule(Board board,
                                         CastlingPieceAlgo<COLOR,PIECE1,Castling> algo) {

        super(board, Impact.Type.CASTLING);
        this.algo = algo;
    }

    protected PieceCastlingImpact<COLOR,PIECE1,PIECE2>
            createImpact(Castling castling, KingPiece<COLOR> king, RookPiece<COLOR> rook) {

        var kPosition = board.getPosition(castling.getKingTarget(), king.getPosition().y());
        var rPosition = board.getPosition(castling.getRookTarget(), rook.getPosition().y());

        @SuppressWarnings("unchecked")
        var impact = new PieceCastlingImpact<>(castling.side(),
                (PIECE1) king, kPosition.get(),
                (PIECE2) rook, rPosition.get()
        );

        return impact;
    }
}