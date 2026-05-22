package com.agutsul.chess.piece.algo;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class EnPassantPositionAlgoAdapter<COLOR extends Color,
                                                PIECE extends Piece<COLOR> & Capturable & EnPassantable>
        implements EnPassantPieceAlgo<COLOR,PIECE,Position> {

    private final Algo<PIECE,Collection<EnPassant>> algo;

    public EnPassantPositionAlgoAdapter(Algo<PIECE,Collection<EnPassant>> algo) {
        this.algo = algo;
    }

    @Override
    public Collection<Position> calculate(PIECE piece) {
        return Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .map(EnPassant::getPosition)
                .toList();
    }
}