package com.agutsul.chess.rule.action;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceCapturePositionActionRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            PIECE1 extends Piece<COLOR1> & Capturable,
                                            PIECE2 extends Piece<COLOR2>>
        extends AbstractCaptureActionRule<COLOR1,COLOR2,PIECE1,PIECE2,
                                          PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>> {

    private final CapturePieceAlgo<COLOR1,PIECE1,Position> algo;

    public PieceCapturePositionActionRule(Board board,
                                          CapturePieceAlgo<COLOR1,PIECE1,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        return List.copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
            createActions(PIECE1 piece1, Collection<Calculated> next) {

        var actions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> getCapturePiece(piece1, (Position) calculated))
                .flatMap(Optional::stream)
                .map(attackedPiece -> new PieceCaptureAction<>(piece1, attackedPiece))
                .collect(toList());

        return actions;
    }

    protected Optional<PIECE2> getCapturePiece(PIECE1 attacker, Position position) {
        @SuppressWarnings("unchecked")
        var capturedPiece = Stream.of(board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(piece -> !Objects.equals(piece.getColor(), attacker.getColor()))
                .map(piece -> (PIECE2) piece)
                .findFirst();

        return capturedPiece;
    }
}