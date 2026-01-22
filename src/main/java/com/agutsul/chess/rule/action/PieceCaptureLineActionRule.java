package com.agutsul.chess.rule.action;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceCaptureLineActionRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                              ATTACKED extends Piece<COLOR2>>
        extends AbstractCaptureActionRule<COLOR1,COLOR2,ATTACKER,ATTACKED,
                                          PieceCaptureAction<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    private final CapturePieceAlgo<COLOR1,ATTACKER,Line> algo;

    public PieceCaptureLineActionRule(Board board,
                                      CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceCaptureAction<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createActions(ATTACKER attacker, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var actions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(line -> Stream.of(board.getPiece(line.getLast()))
                        .flatMap(Optional::stream)
                        .map(attackedPiece -> new PieceCaptureAction<>(
                                attacker, (ATTACKED) attackedPiece, line
                        ))
                )
                .collect(toList());

        return actions;
    }
}