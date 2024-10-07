package com.agutsul.chess.piece.pawn;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractEnPassantActionRule;

class PawnEnPassantActionRule<C1 extends Color,
                              C2 extends Color,
                              P1 extends PawnPiece<C1>,
                              P2 extends PawnPiece<C2>>
        extends AbstractEnPassantActionRule<C1, C2, P1, P2, PieceEnPassantAction<C1,C2,P1,P2>> {

    private final EnPassantPieceAlgo<C1, P1, Position> algo;

    PawnEnPassantActionRule(Board board, EnPassantPieceAlgo<C1, P1, Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Position> calculatePositions(P1 piece) {
        return algo.calculate(piece);
    }

    @Override
    protected PieceEnPassantAction<C1, C2, P1, P2> createAction(P1 pawn1, P2 pawn2, Position position) {
        return new PieceEnPassantAction<C1, C2, P1, P2>(pawn1, pawn2, position);
    }
}