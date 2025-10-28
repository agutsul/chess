package com.agutsul.chess.rule.action;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;

public final class PieceCaptureLineActionRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              PIECE1 extends Piece<COLOR1> & Capturable,
                                              PIECE2 extends Piece<COLOR2>>
        extends AbstractCaptureActionRule<COLOR1,COLOR2,PIECE1,PIECE2,
                                          PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>> {

    private final CapturePieceAlgo<COLOR1,PIECE1,Line> algo;

    public PieceCaptureLineActionRule(Board board,
                                      CapturePieceAlgo<COLOR1,PIECE1,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        return List.copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
            createActions(PIECE1 piece, Collection<Calculated> next) {

        @SuppressWarnings("unchecked")
        var actions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .map(line -> Stream.of(board.getPiece(line.getLast()))
                        .flatMap(Optional::stream)
                        .map(attackedPiece -> new PieceCaptureAction<>(piece, (PIECE2) attackedPiece, line))
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .collect(toList());

        return actions;
    }
}