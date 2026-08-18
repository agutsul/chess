package com.agutsul.chess.board;

import org.apache.commons.lang3.builder.Builder;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface BoardBuilder<POSITION>
        extends Builder<Board> {

    BoardBuilder<POSITION> withPiece(Piece.Type pieceType, Color color, POSITION position);

    BoardBuilder<POSITION> withWhitePiece(Piece.Type pieceType, POSITION position);
    BoardBuilder<POSITION> withBlackPiece(Piece.Type pieceType, POSITION position);

    BoardBuilder<POSITION> withWhiteKing(POSITION position);
    BoardBuilder<POSITION> withWhiteQueen(POSITION position);

    BoardBuilder<POSITION> withWhiteBishop(POSITION position);
    BoardBuilder<POSITION> withWhiteBishops(POSITION position1, POSITION position2);

    BoardBuilder<POSITION> withWhiteKnight(POSITION position);
    BoardBuilder<POSITION> withWhiteKnights(POSITION position1, POSITION position2);

    BoardBuilder<POSITION> withWhiteRook(POSITION position);
    BoardBuilder<POSITION> withWhiteRooks(POSITION position1, POSITION position2);

    BoardBuilder<POSITION> withWhitePawn(POSITION position);
    BoardBuilder<POSITION> withWhitePawns(POSITION position1, POSITION position2, @SuppressWarnings("unchecked") POSITION... positions);

    BoardBuilder<POSITION> withBlackKing(POSITION position);
    BoardBuilder<POSITION> withBlackQueen(POSITION position);

    BoardBuilder<POSITION> withBlackBishop(POSITION position);
    BoardBuilder<POSITION> withBlackBishops(POSITION position1, POSITION position2);

    BoardBuilder<POSITION> withBlackKnight(POSITION position);
    BoardBuilder<POSITION> withBlackKnights(POSITION position1, POSITION position2);

    BoardBuilder<POSITION> withBlackRook(POSITION position);
    BoardBuilder<POSITION> withBlackRooks(POSITION position1, POSITION position2);

    BoardBuilder<POSITION> withBlackPawn(POSITION position);
    BoardBuilder<POSITION> withBlackPawns(POSITION position1, POSITION position2, @SuppressWarnings("unchecked") POSITION... positions);

}