package com.agutsul.chess.antlr.fen;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.agutsul.chess.antlr.AntlrGameListener;
import com.agutsul.chess.antlr.grammar.fenBaseListener;
import com.agutsul.chess.antlr.grammar.fenParser;
import com.agutsul.chess.game.fen.FenGame;

final class FenAntlrListener
        extends fenBaseListener
        implements AntlrGameListener<FenGame<?>> {

    private static final String FEN_LINE_PATTERN = "([p,P,n,N,b,B,r,R,q,Q,k,K,1-8]){1,8}";

    private final List<FenGame<?>> games = new ArrayList<>();
    private final Pattern linePattern;

    private FenGameBuilder gameBuilder;

    FenAntlrListener() {
        this.linePattern = Pattern.compile(FEN_LINE_PATTERN);
    }

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
        var line = ctx.getText();

        var matcher = linePattern.matcher(line);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                    "Unsupported board line: '%s'",
                    line
            ));
        }

        this.gameBuilder.addBoardLine(line);
    }
}