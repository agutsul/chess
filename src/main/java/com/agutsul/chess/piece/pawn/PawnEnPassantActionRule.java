package com.agutsul.chess.piece.pawn;

import java.util.Collection;

import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractEnPassantActionRule;

class PawnEnPassantActionRule<COLOR1 extends Color,
                              COLOR2 extends Color,
                              PAWN1 extends PawnPiece<COLOR1>,
                              PAWN2 extends PawnPiece<COLOR2>>
        extends AbstractEnPassantActionRule<COLOR1, COLOR2, PAWN1, PAWN2,
                                            PieceEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2>> {

    private final EnPassantPieceAlgo<COLOR1, PAWN1, Position> algo;

    PawnEnPassantActionRule(Board board, EnPassantPieceAlgo<COLOR1, PAWN1, Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Position> calculatePositions(PAWN1 piece) {
        return algo.calculate(piece);
    }

    @Override
    protected PieceEnPassantAction<COLOR1, COLOR2, PAWN1, PAWN2> createAction(PAWN1 pawn1,
                                                                              PAWN2 pawn2,
                                                                              Position position) {
        return new PieceEnPassantAction<>(pawn1, pawn2, position);
    }
}