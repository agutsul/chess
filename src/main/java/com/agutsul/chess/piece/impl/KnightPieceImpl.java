package com.agutsul.chess.piece.impl;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.knight.KnightPieceActionRule;
import com.agutsul.chess.piece.knight.KnightPieceImpactRule;
import com.agutsul.chess.position.Position;

final class KnightPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements KnightPiece<COLOR> {

    KnightPieceImpl(Board board, COLOR color, String unicode,
                    Position position, int direction) {

        super(board, Piece.Type.KNIGHT, color, unicode, position, direction,
                new ActivePieceStateImpl<>(board,
                                           new KnightPieceActionRule<>(board),
                                           new KnightPieceImpactRule<>(board)
                )
        );
    }
}