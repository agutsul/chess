package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.impact.PiecePromoteImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface PromoteImpactRule<COLOR  extends Color,
                                   PIECE  extends Piece<COLOR> & Promotable,
                                   IMPACT extends PiecePromoteImpact<COLOR,PIECE>>
        extends Rule<PIECE,Collection<IMPACT>> {

    Set<Piece.Type> PROMOTION_TYPES = EnumSet.of(BISHOP, KNIGHT, ROOK, QUEEN);
}