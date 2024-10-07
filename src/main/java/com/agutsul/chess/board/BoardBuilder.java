package com.agutsul.chess.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceFactory;

/*
 * Should be used mainly in unit test to simplify creating board with specified pieces only
 */
public final class BoardBuilder implements BoardBuilderAdapter {

    private final BoardContext whitePieceContext = new BoardContext();
    private final BoardContext blackPieceContext = new BoardContext();

    @Override
    public Board build() {
        var board = new BoardImpl();

        var pieces = new ArrayList<Piece<Color>>();
        pieces.addAll(createPieces(board.getWhitePieceFactory(), whitePieceContext));
        pieces.addAll(createPieces(board.getBlackPieceFactory(), blackPieceContext));

        board.setPieces(pieces);
        return board;
    }

    @Override
    public BoardBuilderAdapter withWhiteKing(String position) {
        whitePieceContext.setKingPosition(position);
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteQueen(String position) {
        whitePieceContext.setQueenPosition(position);
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteBishop(String position) {
        whitePieceContext.setBishopPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteBishops(String position1, String position2) {
        whitePieceContext.setBishopPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteKnight(String position) {
        whitePieceContext.setKnightPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteKnights(String position1, String position2) {
        whitePieceContext.setKnightPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteRook(String position) {
        whitePieceContext.setRookPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteRooks(String position1, String position2) {
        whitePieceContext.setRookPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhitePawn(String position) {
        whitePieceContext.setPawnPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhitePawns(String position1, String position2, String... positions) {
        return withPawns(whitePieceContext, position1, position2, positions);
    }

    @Override
    public BoardBuilderAdapter withBlackKing(String position) {
        blackPieceContext.setKingPosition(position);
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackQueen(String position) {
        blackPieceContext.setQueenPosition(position);
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackBishop(String position) {
        blackPieceContext.setBishopPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackBishops(String position1, String position2) {
        blackPieceContext.setBishopPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackKnight(String position) {
        blackPieceContext.setKnightPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackKnights(String position1, String position2) {
        blackPieceContext.setBishopPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackRook(String position) {
        blackPieceContext.setRookPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackRooks(String position1, String position2) {
        blackPieceContext.setRookPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackPawn(String position) {
        blackPieceContext.setPawnPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackPawns(String position1, String position2, String... positions) {
        return withPawns(blackPieceContext, position1, position2, positions);
    }

    private BoardBuilderAdapter withPawns(BoardContext context,
            String position1, String position2, String... positions) {

        var pawnPositions = new ArrayList<String>();

        pawnPositions.add(position1);
        pawnPositions.add(position2);
        pawnPositions.addAll(List.of(positions));

        context.setPawnPositions(pawnPositions);
        return this;
    }

    private Collection<Piece<Color>> createPieces(PieceFactory pieceFactory,
                                                  BoardContext context) {
        var pieces = new ArrayList<Piece<Color>>();

        if (context.getKingPosition() != null) {
            pieces.add(pieceFactory.createKing(context.getKingPosition()));
        }

        if (context.getQueenPosition() != null) {
            pieces.add(pieceFactory.createQueen(context.getQueenPosition()));
        }

        if (context.getKnightPositions() != null) {
            pieces.addAll(context.getKnightPositions().stream()
                    .map(position -> pieceFactory.createKnight(position))
                    .toList());
        }

        if (context.getBishopPositions() != null) {
            pieces.addAll(context.getBishopPositions().stream()
                    .map(position -> pieceFactory.createBishop(position))
                    .toList());
        }

        if (context.getRookPositions() != null) {
            pieces.addAll(context.getRookPositions().stream()
                    .map(position -> pieceFactory.createRook(position))
                    .toList());
        }

        if (context.getPawnPositions() != null) {
            pieces.addAll(context.getPawnPositions().stream()
                    .map(position -> pieceFactory.createPawn(position))
                    .toList());
        }

        return pieces;
    }

    private static class BoardContext {

        private String kingPosition;
        private String queenPosition;
        private List<String> bishopPositions;
        private List<String> knightPositions;
        private List<String> rookPositions;
        private List<String> pawnPositions;

        public String getKingPosition() {
            return kingPosition;
        }
        public void setKingPosition(String kingPosition) {
            this.kingPosition = kingPosition;
        }
        public String getQueenPosition() {
            return queenPosition;
        }
        public void setQueenPosition(String queenPosition) {
            this.queenPosition = queenPosition;
        }
        public List<String> getBishopPositions() {
            return bishopPositions;
        }
        public void setBishopPositions(List<String> bishopPositions) {
            this.bishopPositions = bishopPositions;
        }
        public List<String> getKnightPositions() {
            return knightPositions;
        }
        public void setKnightPositions(List<String> knightPositions) {
            this.knightPositions = knightPositions;
        }
        public List<String> getRookPositions() {
            return rookPositions;
        }
        public void setRookPositions(List<String> rookPositions) {
            this.rookPositions = rookPositions;
        }
        public List<String> getPawnPositions() {
            return pawnPositions;
        }
        public void setPawnPositions(List<String> pawnPositions) {
            this.pawnPositions = pawnPositions;
        }
    }
}