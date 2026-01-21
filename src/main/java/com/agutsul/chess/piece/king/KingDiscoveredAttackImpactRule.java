package com.agutsul.chess.piece.king;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.attack.PieceDiscoveredAttackPositionImpactRule;

final class KingDiscoveredAttackImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           PIECE  extends KingPiece<COLOR1>,
                                           ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                           ATTACKED extends Piece<COLOR2>>
        extends PieceDiscoveredAttackPositionImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED> {

    KingDiscoveredAttackImpactRule(Board board,
                                   Algo<PIECE,Collection<Position>> algo) {

        super(board, new KingPieceAlgoAdapter<>(board, algo));
    }
}