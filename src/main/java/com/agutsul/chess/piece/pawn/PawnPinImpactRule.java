package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.BigMovePieceAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.pin.PiecePinPositionImpactRule;

final class PawnPinImpactRule<COLOR1 extends Color,
                              COLOR2 extends Color,
                              PINNED extends PawnPiece<COLOR1>,
                              PIECE  extends Piece<COLOR1>,
                              ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends PiecePinPositionImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,
                                           PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>> {

    @SuppressWarnings("unchecked")
    PawnPinImpactRule(Board board,
                      MovePieceAlgo<COLOR1,PINNED,Position> moveAlgo,
                      BigMovePieceAlgo<COLOR1,PINNED,Position> bigMoveAlgo,
                      CapturePieceAlgo<COLOR1,PINNED,Position> captureAlgo) {

        super(board, new CompositePieceAlgo<>(board,
                moveAlgo, bigMoveAlgo, captureAlgo
        ));
    }
}