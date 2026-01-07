package com.agutsul.chess.activity.impact;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Connectable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceConnectionImpact<COLOR extends Color,
                                   PIECE extends Piece<COLOR> & Connectable>
        extends AbstractSourceActivity<Impact.Type,Collection<PIECE>>
        implements Impact<Collection<PIECE>> {

    public PieceConnectionImpact(PIECE piece1, PIECE piece2,
                                 @SuppressWarnings("unchecked") PIECE... piece3) {

        this(Stream.of(List.of(piece1, piece2), List.of(piece3))
                .flatMap(Collection::stream)
                .toList()
        );
    }

    public PieceConnectionImpact(Collection<PIECE> pieces) {
        super(Impact.Type.CONNECTION, pieces);
    }

    @Override
    public final String toString() {
        return String.format("(%s)", join(getSource(), ":"));
    }

    @Override
    public final Position getPosition() {
        return Stream.of(getSource())
                .flatMap(Collection::stream)
                .map(Piece::getPosition)
                .findFirst()
                .orElse(null);
    }
}