package com.agutsul.chess.piece;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.knight.KnightPieceActionRule;
import com.agutsul.chess.piece.knight.KnightPieceImpactRule;
import com.agutsul.chess.position.Position;

final class KnightPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements KnightPiece<COLOR> {

    KnightPieceImpl(Board board, COLOR color, String unicode, Position position) {
        super(board, Piece.Type.KNIGHT, color, unicode, position,
                new ActivePieceState<>(board,
                                       new KnightPieceActionRule(board),
                                       new KnightPieceImpactRule(board)
                )
        );
    }
}