package com.agutsul.chess.antlr.pgn;

import static org.apache.commons.lang3.StringUtils.strip;

import java.util.ArrayList;
import java.util.List;

import com.agutsul.chess.antlr.AntlrGameListener;
import com.agutsul.chess.antlr.grammar.PGNBaseListener;
import com.agutsul.chess.antlr.grammar.PGNParser;
import com.agutsul.chess.game.pgn.PgnGame;

final class PgnAntlrListener
        extends PGNBaseListener
        implements AntlrGameListener<PgnGame<?>> {

    private static final String EVENT_TAG = "Event";
    private static final String SITE_TAG  = "Site";
    private static final String ROUND_TAG = "Round";
    private static final String WHITE_TAG = "White";
    private static final String BLACK_TAG = "Black";
    private static final String TERMINATION_TAG  = "Termination";
    private static final String TIME_CONTROL_TAG = "TimeControl";

    private final List<PgnGame<?>> games = new ArrayList<>();
    private PgnGameBuilder gameBuilder;

    private int variationDepth;

    @Override
    public List<PgnGame<?>> getGames() {
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
        this.gameBuilder = new PgnGameBuilder();
    }

    @Override
    public void exitPgn_game(PGNParser.Pgn_gameContext ctx) {
        this.games.add(this.gameBuilder.build());
    }

    @Override
    public void enterTag_pair(PGNParser.Tag_pairContext ctx) {
        var tagName = strip(ctx.tag_name().getText());
        var tagValueRaw = strip(ctx.tag_value().getText());

        // STRING tokens starts and ends with " (a quote character)
        var tagValue = tagValueRaw.substring(1, tagValueRaw.length() - 1);

        switch (tagName) {
        case EVENT_TAG        -> this.gameBuilder.withEvent(tagValue);
        case SITE_TAG         -> this.gameBuilder.withSite(tagValue);
        case ROUND_TAG        -> this.gameBuilder.withRound(tagValue);
        case WHITE_TAG        -> this.gameBuilder.withWhitePlayer(tagValue);
        case BLACK_TAG        -> this.gameBuilder.withBlackPlayer(tagValue);
        case TERMINATION_TAG  -> this.gameBuilder.withGameTermination(tagValue);
        case TIME_CONTROL_TAG -> this.gameBuilder.withTimeControl(tagValue);
        default               -> this.gameBuilder.addTag(tagName, tagValue);
        }
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

        this.gameBuilder.addAction(strip(ctx.getText()));
    }

    @Override
    public void enterGame_termination(PGNParser.Game_terminationContext ctx) {
        this.gameBuilder.withGameState(strip(ctx.getText()));
   }
}