package com.agutsul.chess.antlr.pgn;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;

import com.agutsul.chess.antlr.AbstractAntlrGameParser;
import com.agutsul.chess.antlr.grammar.PGNLexer;
import com.agutsul.chess.antlr.grammar.PGNParser;
import com.agutsul.chess.game.pgn.PgnGame;

public final class PgnGameParser
        extends AbstractAntlrGameParser<PgnGame<?>,PGNParser,PgnAntlrListener> {

    private static final Logger LOGGER = getLogger(PgnGameParser.class);

    public PgnGameParser() {
        super(LOGGER);
    }

    @Override
    public List<PgnGame<?>> parse(String string) {
        var gameStrings = new ArrayList<String>();

        var builder = new PgnStringBuilder();
        for (var line : string.split(lineSeparator())) {
            builder.append(strip(line));

            if (builder.isReady()) {
                gameStrings.add(builder.build());
                builder.reset();
            }
        }

        return super.parse(join(gameStrings, lineSeparator()));
    }

    @Override
    protected Lexer createLexer(CharStream input) {
        return new PGNLexer(input);
    }

    @Override
    protected PGNParser createParser(TokenStream input) {
        return new PGNParser(input);
    }

    @Override
    protected PgnAntlrListener createListener() {
        return new PgnAntlrListener();
    }

    @Override
    protected ParserRuleContext createContext(PGNParser parser) {
        return parser.parse();
    }
}