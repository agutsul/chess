package com.agutsul.chess.piece.impl;

import com.agutsul.chess.activity.ActivityContext;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.queen.QueenPieceActionRule;
import com.agutsul.chess.piece.queen.QueenPieceImpactRule;
import com.agutsul.chess.position.Position;

final class QueenPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements QueenPiece<COLOR> {

    QueenPieceImpl(Board board, COLOR color, String unicode,
                   Position position, ActivityContext context) {

        super(board, position,
                new PieceContext<>(Piece.Type.QUEEN, color, unicode, context.getDirection()),
                new ActivePieceStateImpl<>(board,
                                           new QueenPieceActionRule<>(board),
                                           new QueenPieceImpactRule<>(board, context.getPromotionLine(),
                                                   context.getOutpostLines()
                                           )
                )
        );
    }
}