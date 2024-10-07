package com.agutsul.chess.piece;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.king.KingPieceActionRule;
import com.agutsul.chess.piece.king.KingPieceImpactRule;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.checkmate.CheckMateEvaluator;
import com.agutsul.chess.rule.checkmate.CompositeCheckMateEvaluator;

final class KingPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements KingPiece<COLOR> {

    private final CheckMateEvaluator<COLOR, KingPiece<COLOR>> checkMateEvaluator;

    KingPieceImpl(Board board, COLOR color, String unicode, Position position) {
        super(board, Piece.Type.KING, color, unicode, position,
                new KingPieceActionRule(board),
                new KingPieceImpactRule(board)
        );

        this.checkMateEvaluator =
                new CompositeCheckMateEvaluator<COLOR, KingPiece<COLOR>>(board);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isChecked() {
        return board.isAttacked((Piece<Color>) this);
    }

    @Override
    public boolean isCheckMated() {
        if (!isChecked()) {
            return false;
        }

        return checkMateEvaluator.evaluate(this);
    }

    @Override
    public final void dispose() {
        throw new UnsupportedOperationException("Unable to dispose KING piece");
    }
}