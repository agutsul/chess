package com.agutsul.chess.fen;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.agutsul.chess.game.Game;

final class FenAntlrListener
        extends fenBaseListener {

    private static final String FEN_LINE_PATTERN = "([p,P,n,N,b,B,r,R,q,Q,k,K,1-8]){1,8}";
    private static final String SKIP_SYMBOL = "-";

    private final List<Game> games = new ArrayList<>();
    private final Pattern linePattern;

    private FenGameBuilder gameBuilder;

    FenAntlrListener() {
        this.linePattern = Pattern.compile(FEN_LINE_PATTERN);
    }

    public List<Game> getGames() {
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
        if (!SKIP_SYMBOL.equals(ctx.getText())) {
            this.gameBuilder.withCastling(ctx.getText());
        }
    }

    @Override
    public void enterEnpassant(fenParser.EnpassantContext ctx) {
//        log("enterEnpassant", ctx.getText());   // enpassant
//        this.gameBuilder.withEnPassant(ctx.getText());
    }

    @Override
    public void enterPosition(fenParser.PositionContext ctx) {
        if (!SKIP_SYMBOL.equals(ctx.getText())) {
            this.gameBuilder.withEnPassant(ctx.getText());
        }
    }

    @Override
    public void enterHalfmoveclock(fenParser.HalfmoveclockContext ctx) {
        if (!SKIP_SYMBOL.equals(ctx.getText())) {
            this.gameBuilder.withHalfMoves(toInt(ctx.getText()));
        }
    }

    @Override
    public void enterFullmoveclock(fenParser.FullmoveclockContext ctx) {
        if (!SKIP_SYMBOL.equals(ctx.getText())) {
            this.gameBuilder.withFullMoves(toInt(ctx.getText()));
        }
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

    @Override
    public void enterPiece(fenParser.PieceContext ctx) {
//        log("enterPiece", ctx.getText());
    }

    @Override
    public void exitPiece(fenParser.PieceContext ctx) {
//        log("exitPiece", ctx.getText());
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
//        log("enterEveryRule", ctx.getText());
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
//        log("exitEveryRule", ctx.getText());
    }

    @Override
    public void visitTerminal(TerminalNode node) {
//        log("visitTerminal", node.getText());
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
//        log("visitErrorNode", node.getText());
    }

//    private static void log(String tagName, String tagValue) {
//        System.out.println(String.format("<%s>%s</%s>", tagName, tagValue, tagName));
//    }
}