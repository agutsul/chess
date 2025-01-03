package com.agutsul.chess.piece;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.bishop.BishopPieceActionRule;
import com.agutsul.chess.piece.bishop.BishopPieceImpactRule;
import com.agutsul.chess.position.Position;

final class BishopPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements BishopPiece<COLOR> {

    BishopPieceImpl(Board board, COLOR color, String unicode,
                    Position position, int direction) {

        super(board, Piece.Type.BISHOP, color, unicode, position, direction,
                new ActivePieceState<>(board,
                                       new BishopPieceActionRule(board),
                                       new BishopPieceImpactRule(board)
                )
        );
    }
}