package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;

final class PawnPromoteAlgo<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN, Position>
        implements PromotePieceAlgo<COLOR, PAWN, Position> {

    private final Collection<Algo<PAWN, Collection<Position>>> algos;
    private final int promotionLine;

    PawnPromoteAlgo(Board board, int promotionLine,
                    MovePieceAlgo<COLOR, PAWN, Position> moveAlgo,
                    CapturePieceAlgo<COLOR, PAWN, Position> captureAlgo) {

        super(board);
        this.promotionLine = promotionLine;
        this.algos = List.of(moveAlgo, captureAlgo);
    }

    @Override
    public Collection<Position> calculate(PAWN pawn) {
        return algos.stream()
                .map(algo -> algo.calculate(pawn))
                .flatMap(Collection::stream)
                .filter(position -> position.y() == promotionLine)
                .collect(toList());
    }
}