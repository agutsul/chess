package com.agutsul.chess.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceFactory;

/*
 * Should be used mainly in unit test to simplify creating board with specified pieces only
 */
public final class BoardBuilder
        implements BoardBuilderAdapter {

    private static final Logger LOGGER = getLogger(BoardBuilder.class);

    private final BoardContext whitePieceContext = new BoardContext();
    private final BoardContext blackPieceContext = new BoardContext();

    @Override
    public Board build() {
        LOGGER.info("Building board started ...");
        var board = new BoardImpl();

        var pieces = new ArrayList<Piece<Color>>();
        pieces.addAll(createPieces(board.getWhitePieceFactory(), whitePieceContext));
        pieces.addAll(createPieces(board.getBlackPieceFactory(), blackPieceContext));

        board.setPieces(pieces);

        LOGGER.info("Building board finished");
        return board;
    }

    @Override
    public BoardBuilderAdapter withWhiteKing(String position) {
        whitePieceContext.setKingPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteQueen(String position) {
        whitePieceContext.setQueenPositions(List.of(position));
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
        blackPieceContext.setKingPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackQueen(String position) {
        blackPieceContext.setQueenPositions(List.of(position));
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

    private static Collection<Piece<Color>> createPieces(PieceFactory pieceFactory,
                                                         BoardContext context) {
        var pieceFactoryPairs = List.of(
                pair(context.getKingPositions(),   position -> pieceFactory.createKing(position)),
                pair(context.getQueenPositions(),  position -> pieceFactory.createQueen(position)),
                pair(context.getKnightPositions(), position -> pieceFactory.createKnight(position)),
                pair(context.getBishopPositions(), position -> pieceFactory.createBishop(position)),
                pair(context.getRookPositions(),   position -> pieceFactory.createRook(position)),
                pair(context.getPawnPositions(),   position -> pieceFactory.createPawn(position))
            );

        var pieces = new ArrayList<Piece<Color>>();
        for (var pair : pieceFactoryPairs) {
            if (pair.getKey() != null) {
                pieces.addAll(createPieces(pair.getKey(), pair.getValue()));
            }
        }

        return pieces;
    }

    private static Pair<List<String>, Function<String, Piece<Color>>> pair(List<String> positions,
                                                                           Function<String, Piece<Color>> function) {
        return Pair.of(positions, function);
    }

    private static List<Piece<Color>> createPieces(List<String> positions,
                                                   Function<String, Piece<Color>> function) {

        return positions.stream()
                .map(position -> function.apply(position))
                .toList();
    }

    private static class BoardContext {

        private List<String> kingPositions;
        private List<String> queenPositions;
        private List<String> bishopPositions;
        private List<String> knightPositions;
        private List<String> rookPositions;
        private List<String> pawnPositions;

        public List<String> getKingPositions() {
            return kingPositions;
        }
        public void setKingPositions(List<String> kingPositions) {
            this.kingPositions = kingPositions;
        }
        public List<String> getQueenPositions() {
            return queenPositions;
        }
        public void setQueenPositions(List<String> queenPositions) {
            this.queenPositions = queenPositions;
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