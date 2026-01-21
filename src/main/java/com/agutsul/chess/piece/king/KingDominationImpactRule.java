package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.domination.PieceDominationPositionImpactRule;

final class KingDominationImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends KingPiece<COLOR1>,
                                     ATTACKED extends Piece<COLOR2>,
                                     IMPACT extends PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceDominationPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    KingDominationImpactRule(Board board,
                             CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {

        super(board, new KingPieceAlgoAdapter<>(board, algo));
    }
}