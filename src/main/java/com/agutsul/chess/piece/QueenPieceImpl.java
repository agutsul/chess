package com.agutsul.chess.piece;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.queen.QueenPieceActionRule;
import com.agutsul.chess.piece.queen.QueenPieceImpactRule;
import com.agutsul.chess.position.Position;

final class QueenPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements QueenPiece<COLOR> {

    QueenPieceImpl(Board board, COLOR color, String unicode, Position position) {
        super(board, Piece.Type.QUEEN, color, unicode, position,
                new ActivePieceState<>(board,
                                       new QueenPieceActionRule(board),
                                       new QueenPieceImpactRule(board)
                )
        );
    }
}