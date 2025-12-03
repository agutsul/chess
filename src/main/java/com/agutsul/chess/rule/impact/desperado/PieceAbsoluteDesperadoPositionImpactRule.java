package com.agutsul.chess.rule.impact.desperado;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceAbsoluteDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceAbsoluteDesperadoPositionImpactRule<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      DESPERADO extends Piece<COLOR1> & Capturable,
                                                      ATTACKER extends Piece<COLOR2> & Capturable,
                                                      ATTACKED extends Piece<COLOR2>>
        extends AbstractDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

    public PieceAbsoluteDesperadoPositionImpactRule(Board board,
                                                    Algo<DESPERADO,Collection<Position>> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            createImpacts(DESPERADO piece, Collection<Calculatable> next) {

        Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                .map(opponentPiece -> findProtectImpacts(opponentPiece))
                .flatMap(Collection::stream)
                .map(impact -> createImpact(Mode.ABSOLUTE, piece, impact))
                .map(PieceAbsoluteDesperadoImpact::new)
                .map(impact -> (PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>) impact)
                .collect(toList());

        return impacts;
    }
}