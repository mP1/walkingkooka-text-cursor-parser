/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package walkingkooka.text.cursor.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.text.CaseSensitivity;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class AndEmptyTextCursorParserTest extends ParserWrapperTestCase<AndEmptyTextCursorParser<ParserContext>>
        implements HashCodeEqualsDefinedTesting2<AndEmptyTextCursorParser<ParserContext>> {

    private final static String STRING = "abc";
    private final static Parser<ParserContext> WRAPPED = Parsers.string(STRING, CaseSensitivity.SENSITIVE);

    @Test
    public void testWithAndEmptyTextCursorParserSame() {
        final AndEmptyTextCursorParser<ParserContext> parser = AndEmptyTextCursorParser.with(WRAPPED);

        assertSame(
                parser,
                AndEmptyTextCursorParser.with(parser)
        );
    }

    @Test
    public void testWithAndEmptyTextCursorSame() {
        final AndEmptyTextCursorParser<ParserContext> parser = AndEmptyTextCursorParser.with(WRAPPED);

        assertSame(
                parser,
                parser.andEmptyTextCursor()
        );
    }

    @Test
    public void testWithAndEmptyTextCursorParserDefaultMethod() {
        final Parser<ParserContext> parser = WRAPPED.andEmptyTextCursor();

        assertSame(
                AndEmptyTextCursorParser.class.getName(),
                parser.getClass().getName(),
                () -> "" + parser
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseWrapperEmpties() {
        this.parseAndCheck(
                STRING,
                ParserTokens.string(STRING, STRING),
                STRING
        );
    }

    @Test
    public void testParseWrapperMatchesButTextCursorIsNotEmpty() {
        this.parseFailAndCheck(STRING + "!");
    }

    @Override
    AndEmptyTextCursorParser<ParserContext> createParser(final Parser<ParserContext> parser) {
        return AndEmptyTextCursorParser.with(parser);
    }

    @Override
    Parser<ParserContext> wrappedParser() {
        return WRAPPED;
    }

    // hashCode/Equals..................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
                AndEmptyTextCursorParser.with(Parsers.fake()),
                AndEmptyTextCursorParser.with(Parsers.fake())
        );
    }

    @Test
    public void testEqualsDifferentToString() {
        this.checkNotEquals(
                AndEmptyTextCursorParser.with(WRAPPED),
                AndEmptyTextCursorParser.with(WRAPPED).setToString("different")
        );
    }

    @Override
    public AndEmptyTextCursorParser<ParserContext> createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), WRAPPED.toString());
    }

    // class............................................................................................................

    @Override
    public Class<AndEmptyTextCursorParser<ParserContext>> type() {
        return Cast.to(AndEmptyTextCursorParser.class);
    }
}
