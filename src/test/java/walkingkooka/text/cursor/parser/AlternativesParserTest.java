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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CaseSensitivity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AlternativesParserTest extends ParserTestCase<AlternativesParser<ParserContext>
        > {

    private final static String TEXT1 = "abc";
    private final static String TEXT2 = "xyz";
    private final static String TEXT3 = "333";
    private final static String TEXT4 = "444";

    private final static Parser<ParserContext> PARSER1 = parser(TEXT1);
    private final static Parser<ParserContext> PARSER2 = parser(TEXT2);
    private final static Parser<ParserContext> PARSER3 = parser(TEXT3);
    private final static Parser<ParserContext> PARSER4 = parser(TEXT4);

    @Test
    public void testWithNullParsersFails() {
        assertThrows(
                NullPointerException.class,
                () -> AlternativesParser.with(null)
        );
    }

    @Test
    public void testWithZeroParsersFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> AlternativesParser.with(
                        Lists.empty()
                )
        );
    }

    @Test
    public void testWithOneNeverWrapped() {
        assertSame(
                PARSER1,
                AlternativesParser.with(
                        Lists.of(
                                PARSER1.cast()
                        )
                )
        );
    }

    @Test
    public void testWith() {
        final List<Parser<ParserContext>> parsers = Lists.of(PARSER1, PARSER2);
        final AlternativesParser<ParserContext> parser = AlternativesParser.with(parsers).cast();
        assertNotSame(
                parsers,
                parser.parsers
        );
        this.checkEquals(
                parsers,
                parser.parsers
        );
    }

    @Test
    public void testWithAllCustomToStringParsers() {
        final List<Parser<ParserContext>> parsers = Lists.of(
                PARSER1.setToString("1"),
                PARSER2.setToString("2")
        );
        final AlternativesParser<ParserContext> custom = AlternativesParser.with(parsers)
                .cast();

        this.checkNotEquals(
                Lists.of(
                        PARSER1,
                        PARSER2
                ),
                custom.parsers,
                "parsers"
        );
        this.checkEquals(
                "1 | 2",
                custom.toString(),
                "custom toString"
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseNone() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseNone2() {
        this.parseFailAndCheck("ab");
    }

    @Test
    public void testParseFirstMatch() {
        this.parseAndCheck(
                TEXT1,
                string(TEXT1),
                TEXT1,
                ""
        );
    }

    @Test
    public void testParseSecondMatch() {
        this.parseAndCheck(
                TEXT2,
                string(TEXT2),
                TEXT2,
                ""
        );
    }

    @Test
    public void testParseSecondIgnoreExtra() {
        final String extra = "!!!";
        this.parseAndCheck(
                TEXT2 + extra,
                string(TEXT2),
                TEXT2,
                extra
        );
    }

    @Test
    public void testParseEmptyParserReporter() {
        // AlternativesParser must not short circuit and skip trying all its parsers when its empty.
        this.parseThrowsEndOfText(
                PARSER1.orReport(ParserReporters.basic()).cast(),
                "",
                1,
                1
        );
    }

    @Test
    public void testParseAllCustomToStringParsers() {
        this.testParseAllCustomToStringParsers(TEXT1);
    }

    @Test
    public void testParseAllCustomToStringParsers2() {
        this.testParseAllCustomToStringParsers(TEXT2);
    }

    private void testParseAllCustomToStringParsers(final String text) {
        this.parseAndCheck(
                this.createParser1(
                        PARSER1.setToString("1"),
                        PARSER2.setToString("2")
                ),
                text,
                string(text),
                text
        );
    }

    @Test
    public void testMinCount() {
        this.minCountAndCheck(
                this.createParser(),
                1
        );
    }

    @Test
    public void testMaxCount() {
        this.maxCountAndCheck(
                this.createParser(),
                1
        );
    }

    @Test
    @Override
    public void testOr() {
        final AlternativesParser<ParserContext> parser = createParser();

        final Parser<ParserContext> parser3 = parser("text3");
        this.checkEquals(
                this.createParser0(
                        PARSER1,
                        PARSER2,
                        parser3
                ),
                parser.or(parser3)
        );
    }

    @Override
    public AlternativesParser<ParserContext> createParser() {
        return this.createParser0(
                PARSER1,
                PARSER2
        );
    }

    @SafeVarargs
    private AlternativesParser<ParserContext> createParser0(final Parser<ParserContext>... parsers) {
        return this.createParser1(parsers).cast();
    }

    @SafeVarargs
    private Parser<ParserContext> createParser1(final Parser<ParserContext>... parsers) {
        return AlternativesParser.with(
                Cast.to(
                        Lists.of(parsers)
                )
        );
    }

    private static StringParserToken string(final String s) {
        return ParserTokens.string(s, s);
    }

    private static Parser<ParserContext> parser(final String string) {
        return Parsers.string(
                string,
                CaseSensitivity.SENSITIVE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createParser(),
                PARSER1 + " | " + PARSER2
        );
    }

    @Test
    public void testToStringSurroundSequenceParser() {
        this.toStringAndCheck(
                AlternativesParser.with(
                        Lists.of(
                                PARSER1,
                                SequenceParser.with(
                                        Lists.of(
                                                PARSER2,
                                                PARSER3
                                        )
                                ),
                                PARSER4
                        )
                ),
                PARSER1 + " | (" + PARSER2 + ", " + PARSER3 + ") | " + PARSER4
        );
    }

    // class............................................................................................................

    @Override
    public Class<AlternativesParser<ParserContext>> type() {
        return Cast.to(AlternativesParser.class);
    }
}
