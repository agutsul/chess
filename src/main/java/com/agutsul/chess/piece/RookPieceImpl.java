package com.agutsul.chess.piece;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.rook.RookPieceActionRule;
import com.agutsul.chess.piece.rook.RookPieceImpactRule;
import com.agutsul.chess.position.Position;

final class RookPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements RookPiece<COLOR> {

    RookPieceImpl(Board board, COLOR color, String unicode, Position position) {
        super(board, Piece.Type.ROOK, color, unicode, position,
                new RookPieceActionRule(board),
                new RookPieceImpactRule(board)
        );
    }
}