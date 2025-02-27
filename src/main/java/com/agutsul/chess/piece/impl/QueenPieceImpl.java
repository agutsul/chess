package com.agutsul.chess.piece.impl;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.piece.queen.QueenPieceActionRule;
import com.agutsul.chess.piece.queen.QueenPieceImpactRule;
import com.agutsul.chess.position.Position;

final class QueenPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements QueenPiece<COLOR> {

    QueenPieceImpl(Board board, COLOR color, String unicode,
                   Position position, int direction) {

        super(board, Piece.Type.QUEEN, color, unicode, position, direction,
                new ActivePieceStateImpl<>(board,
                                           new QueenPieceActionRule<>(board),
                                           new QueenPieceImpactRule<>(board)
                )
        );
    }
}