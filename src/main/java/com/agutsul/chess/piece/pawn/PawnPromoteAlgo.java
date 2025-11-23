package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnPromoteAlgo<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN,Position>
        implements PromotePieceAlgo<COLOR,PAWN,Position> {

    private final CompositePieceAlgo<COLOR,PAWN,Position> algo;
    private final int promotionLine;

    @SuppressWarnings("unchecked")
    PawnPromoteAlgo(Board board, int promotionLine,
                    MovePieceAlgo<COLOR,PAWN,Position> moveAlgo,
                    CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo) {

        super(board);
        this.promotionLine = promotionLine;
        this.algo = new CompositePieceAlgo<>(board, moveAlgo, captureAlgo);
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        return Stream.of(algo.calculate(pawn))
                .flatMap(Collection::stream)
                .filter(position -> position.y() == promotionLine)
                .toList();
    }
}