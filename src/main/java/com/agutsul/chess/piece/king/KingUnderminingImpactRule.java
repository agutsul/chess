package com.agutsul.chess.piece.king;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.undermining.PieceUnderminingPositionImpactRule;

final class KingUnderminingImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends KingPiece<COLOR1>,
                                      ATTACKED extends Piece<COLOR2>>
        extends PieceUnderminingPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    KingUnderminingImpactRule(Board board,
                              CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board, algo);
    }

    @Override
    protected boolean isPieceAttackable(Piece<Color> piece) {
        return super.isPieceAttackable(piece) && !((Protectable) piece).isProtected();
    }
}