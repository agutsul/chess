package com.agutsul.chess.piece.impl;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.bishop.BishopPieceActionRule;
import com.agutsul.chess.piece.bishop.BishopPieceImpactRule;
import com.agutsul.chess.position.Position;

final class BishopPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements BishopPiece<COLOR> {

    BishopPieceImpl(Board board, COLOR color, String unicode,
                    Position position, int direction) {

        super(board, Piece.Type.BISHOP, color, unicode, position, direction,
                new ActivePieceStateImpl<>(board,
                                           new BishopPieceActionRule<>(board),
                                           new BishopPieceImpactRule<>(board)
                )
        );
    }
}