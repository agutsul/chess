package com.agutsul.chess.antlr.fen;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.List;

import com.agutsul.chess.antlr.AntlrGameListener;
import com.agutsul.chess.antlr.grammar.fenBaseListener;
import com.agutsul.chess.antlr.grammar.fenParser;
import com.agutsul.chess.game.fen.FenGame;

final class FenAntlrListener
        extends fenBaseListener
        implements AntlrGameListener<FenGame<?>> {

    private final List<FenGame<?>> games = new ArrayList<>();

    private FenGameBuilder gameBuilder;

    @Override
    public List<FenGame<?>> getGames() {
        return this.games;
    }

    @Override
    public void enterFen(fenParser.FenContext ctx) {
        this.gameBuilder = new FenGameBuilder();
    }

    @Override
    public void exitFen(fenParser.FenContext ctx) {
        this.games.add(this.gameBuilder.build());
    }

    @Override
    public void enterColor(fenParser.ColorContext ctx) {
        this.gameBuilder.withActiveColor(ctx.getText());
    }

    @Override
    public void enterCastling(fenParser.CastlingContext ctx) {
        this.gameBuilder.withCastling(ctx.getText());
    }

    @Override
    public void enterEnpassant(fenParser.EnpassantContext ctx) {
        this.gameBuilder.withEnPassant(ctx.getText());
    }

    @Override
    public void enterPosition(fenParser.PositionContext ctx) {
        this.gameBuilder.withEnPassantPosition(ctx.getText());
    }

    @Override
    public void enterHalfmoveclock(fenParser.HalfmoveclockContext ctx) {
        this.gameBuilder.withHalfMoves(toInt(ctx.getText()));
    }

    @Override
    public void enterFullmoveclock(fenParser.FullmoveclockContext ctx) {
        this.gameBuilder.withFullMoves(toInt(ctx.getText()));
    }

    @Override
    public void enterRank(fenParser.RankContext ctx) {
        this.gameBuilder.addBoardLine(ctx.getText());
    }
}