package com.agutsul.chess.pgn;

import java.util.ArrayList;
import java.util.List;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;

final class PgnAntlrListener
        extends PGNBaseListener {

    private static final String EVENT_TAG = "Event";
    private static final String SITE_TAG = "Site";
    private static final String ROUND_TAG = "Round";
    private static final String TERMINATION_TAG = "Termination";

    private final List<Game> games = new ArrayList<>();
    private PgnGameBuilder gameBuilder;

    private int variationDepth;

    public List<Game> getGames() {
        return this.games;
    }

    /**
     * Checks if the listener is currently inside a RAV (Recursive Annotation Variation)
     *
     * @return {@code true} iff the listener is currently inside a RAV (Recursive Annotation Variation)
     */
    public boolean isInVariation() {
        return this.variationDepth != 0;
    }

    @Override
    public void enterPgn_game(PGNParser.Pgn_gameContext ctx) {
//        System.out.println(ctx.getText());
        this.gameBuilder = new PgnGameBuilder();
    }

    @Override
    public void exitPgn_game(PGNParser.Pgn_gameContext ctx) {
        this.games.add(this.gameBuilder.build());
    }

    @Override
    public void enterTag_pair(PGNParser.Tag_pairContext ctx) {
        var tagName = ctx.tag_name().getText();
        var tagValueRaw = ctx.tag_value().getText();

        // STRING tokens starts and ends with " (a quote character)
        var tagValue = tagValueRaw.substring(1, tagValueRaw.length() - 1);

        if (EVENT_TAG.equalsIgnoreCase(tagName)) {
            this.gameBuilder.withEvent(tagValue);
        }

        if (SITE_TAG.equalsIgnoreCase(tagName)) {
            this.gameBuilder.withSite(tagValue);
        }

        if (ROUND_TAG.equalsIgnoreCase(tagName)) {
            this.gameBuilder.withRound(tagValue);
        }

        if (Colors.WHITE.name().equalsIgnoreCase(tagName)) {
            this.gameBuilder.withWhitePlayer(tagValue);
        }

        if (Colors.BLACK.name().equalsIgnoreCase(tagName)) {
            this.gameBuilder.withBlackPlayer(tagValue);
        }

        if (TERMINATION_TAG.equalsIgnoreCase(tagName)) {
            this.gameBuilder.withGameTermination(tagValue);
        }

        this.gameBuilder.addTag(tagName, tagValue);
    }

    @Override
    public void enterRecursive_variation(PGNParser.Recursive_variationContext ctx) {
        this.variationDepth++;
    }

    @Override
    public void exitRecursive_variation(PGNParser.Recursive_variationContext ctx) {
        this.variationDepth--;
    }

    @Override
    public void enterSan_move(PGNParser.San_moveContext ctx) {
        // currently, moves inside any RAVs (Recursive Annotation Variation) are just ignored
        if (isInVariation()) {
            return;
        }

        this.gameBuilder.addAction(ctx.getText());
    }

    @Override
    public void enterGame_termination(PGNParser.Game_terminationContext ctx) {
        this.gameBuilder.withGameState(ctx.getText());
   }
}