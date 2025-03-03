package com.agutsul.chess.antlr.fen;

import static org.slf4j.LoggerFactory.getLogger;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;

import com.agutsul.chess.antlr.AbstractAntlrGameParser;
import com.agutsul.chess.antlr.grammar.fenLexer;
import com.agutsul.chess.antlr.grammar.fenParser;
import com.agutsul.chess.game.fen.FenGame;

public final class FenGameParser
        extends AbstractAntlrGameParser<FenGame,fenParser,FenAntlrListener> {

    private static final Logger LOGGER = getLogger(FenGameParser.class);

    public FenGameParser() {
        super(LOGGER);
    }

    @Override
    protected Lexer createLexer(CharStream input) {
        return new fenLexer(input);
    }

    @Override
    protected fenParser createParser(TokenStream input) {
        return new fenParser(input);
    }

    @Override
    protected FenAntlrListener createListener() {
        return new FenAntlrListener();
    }

    @Override
    protected ParserRuleContext createContext(fenParser parser) {
        return parser.fen();
    }
}