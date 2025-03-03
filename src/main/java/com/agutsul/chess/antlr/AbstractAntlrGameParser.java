package com.agutsul.chess.antlr;

import static java.util.Collections.emptyList;
import static org.antlr.v4.runtime.CharStreams.fromReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;

import com.agutsul.chess.game.Game;

public abstract class AbstractAntlrGameParser<GAME extends Game,
                                              PARSER extends Parser,
                                              LISTENER extends ParseTreeListener & AntlrGameListener<GAME>>
        implements AntlrParser<GAME,String> {

    protected final Logger logger;

    protected AbstractAntlrGameParser(Logger logger) {
        this.logger = logger;
    }

    protected abstract Lexer createLexer(CharStream input);
    protected abstract PARSER createParser(TokenStream input);
    protected abstract LISTENER createListener();
    protected abstract ParserRuleContext createContext(PARSER parser);

    @Override
    public List<GAME> parse(String string) {
        try (var reader = new StringReader(string)) {
            var lexer = createLexer(fromReader(reader));

            var parser = createParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();

            var errorListener = new AntlrErrorListener();
            parser.addErrorListener(errorListener);

            LISTENER gameListener = createListener();
            ParseTreeWalker.DEFAULT.walk(gameListener, createContext(parser));

            if (!errorListener.hasAnyErrors()) {
                return gameListener.getGames();
            }

            var message = String.format("Parsing: '%s'. Errors: %s",
                    string,
                    errorListener.getErrors()
            );

            logger.error(message);
        } catch (IOException e) {
            logger.error(String.format("Exception parsing: '%s'", string), e);
        }

        return emptyList();
    }
}