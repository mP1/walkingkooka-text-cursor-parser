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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CaseSensitivity;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SequenceParserTest extends NonEmptyParserTestCase<SequenceParser<ParserContext>, SequenceParserToken>
        implements HashCodeEqualsDefinedTesting2<SequenceParser<ParserContext>> {

    private final static String TEXT1 = "abc";
    private final static String TEXT2 = "xyz";
    private final static String TEXT3 = "123";
    private final static String TEXT4 = "456";

    private final static Parser<ParserContext> PARSER1 = parser(TEXT1);
    private final static Parser<ParserContext> PARSER2 = parser(TEXT2);
    private final static Parser<ParserContext> PARSER3 = parser(TEXT3);
    private final static Parser<ParserContext> PARSER4 = parser(TEXT4);

    private final static StringParserToken TOKEN1 = string(TEXT1);
    private final static StringParserToken TOKEN2 = string(TEXT2);
    private final static StringParserToken TOKEN3 = string(TEXT3);
    private final static StringParserToken TOKEN4 = string(TEXT4);

    private final static SequenceParserToken SEQUENCE_MISSING = ParserTokens.sequence(
            Lists.of(TOKEN1, TOKEN2),
            TEXT1 + TEXT2);
    private final static SequenceParserToken SEQUENCE_TOKEN3 = ParserTokens.sequence(
            Lists.of(TOKEN1, TOKEN2, TOKEN3),
            TEXT1 + TEXT2 + TEXT3);

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SequenceParser.with(null)
        );
    }

    @Test
    public void testWithFlattens() {
        final Parser<ParserContext> wrapped = SequenceParser.with(
                Lists.of(
                        PARSER2,
                        PARSER3
                )
        );
        final SequenceParser<ParserContext> wrapper = SequenceParser.with(
                Lists.of(
                        PARSER1,
                        wrapped,
                        PARSER4
                )
        ).cast();

        this.checkEquals(
                Lists.of(
                        PARSER1,
                        PARSER2,
                        PARSER3,
                        PARSER4
                ),
                wrapper.parsers
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
    public void testParseMissingRequired() {
        this.parseFailAndCheck(TEXT1);
    }

    @Test
    public void testParseMissingRequired2() {
        this.parseFailAndCheck(TEXT1 + "x");
    }

    @Test
    public void testParseMissingRequired3() {
        this.parseFailAndCheck(TEXT1 + "xy");
    }

    @Test
    public void testParseMissingRequired4() {
        this.parseFailAndCheck(TEXT1 + TEXT3);
    }

    @Test
    public void testParseMissingOptionalFirst() {
        final String text = TEXT2 + TEXT1;

        this.parseAndCheck(
                PARSER3.optional()
                        .and(PARSER2)
                        .and(PARSER1),
                this.createContext(),
                text,
                ParserTokens.sequence(
                        Lists.of(
                                TOKEN2,
                                TOKEN1
                        ),
                        text
                ),
                text
        );
    }

    @Test
    public void testParseAllOptionalFail() {
        this.parseFailAndCheck(
                PARSER3.optional()
                        .and(
                                PARSER2.optional()
                        ).and(
                                PARSER1.optional()
                        ),
                "!@#"
        );
    }

    @Test
    public void testParseOutOfOrder() {
        this.parseFailAndCheck(TEXT2 + TEXT1);
    }

    @Test
    public void testParseOutOfOrder2() {
        this.parseFailAndCheck(TEXT3 + TEXT2 + TEXT1);
    }

    @Test
    public void testParseAllRequiredMissingOptional() {
        final String text = TEXT1 + TEXT2;
        this.parseAndCheck(
                text,
                SEQUENCE_MISSING,
                text,
                ""
        );
    }

    @Test
    public void testParseAllRequiredMissingOptional2() {
        final String text = TEXT1 + TEXT2;
        final String textAfter = "...";
        this.parseAndCheck(
                text + textAfter,
                SEQUENCE_MISSING,
                text,
                textAfter
        );
    }

    @Test
    public void testParseAllRequiredMissingOptional3() {
        final String text = TEXT1 + TEXT2;
        final String textAfter = "12";
        this.parseAndCheck(
                text + textAfter,
                SEQUENCE_MISSING,
                text,
                textAfter
        );
    }

    @Test
    public void testParseAllRequiredAndOptional() {
        final String text = TEXT1 + TEXT2 + TEXT3;
        this.parseAndCheck(
                text,
                SEQUENCE_TOKEN3,
                text,
                ""
        );
    }

    @Test
    public void testParseAllRequiredAndOptional2() {
        final String text = TEXT1 + TEXT2 + TEXT3;
        final String textAfter = "...";
        this.parseAndCheck(
                text + textAfter,
                SEQUENCE_TOKEN3,
                text,
                textAfter
        );
    }

    @Override
    public SequenceParser<ParserContext> createParser() {
        return Cast.to(
                SequenceParser.with(
                        Lists.of(
                                PARSER1,
                                PARSER2,
                                PARSER3.optional()
                        )
                )
        );
    }

    // and..............................................................................................................

    @Test
    public void testAndParser() {
        final Parser<ParserContext> aaa = parser("AAA");
        final Parser<ParserContext> bbb = parser("BBB");
        final Parser<ParserContext> and = parser("ZZZ");

        this.andAndCheck(
                SequenceParser.with(
                        Lists.of(
                                aaa,
                                bbb
                        )
                ),
                and,
                Parsers.sequence(
                        Lists.of(
                                aaa,
                                bbb,
                                and
                        )
                )
        );
    }

    @Test
    public void testAndSequenceParser() {
        final Parser<ParserContext> aaa = parser("AAA");
        final Parser<ParserContext> bbb = parser("BBB");
        final Parser<ParserContext> ccc = parser("CCC");
        final Parser<ParserContext> ddd = parser("DDD");

        this.andAndCheck(
                SequenceParser.with(
                        Lists.of(
                                aaa,
                                bbb
                        )
                ),
                Parsers.sequence(
                        Lists.of(
                                ccc,
                                ddd
                        )
                ),
                Parsers.sequence(
                        Lists.of(
                                aaa,
                                bbb,
                                ccc,
                                ddd
                        )
                )
        );
    }

    private static Parser<ParserContext> parser(final String string) {
        return Parsers.string(
                string,
                CaseSensitivity.SENSITIVE
        );
    }

    private static StringParserToken string(final String s) {
        return ParserTokens.string(s, s);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualWithoutNames() {
        this.checkEquals(
                SequenceParser.with(
                        Lists.of(
                                PARSER1,
                                PARSER2,
                                PARSER3.optional()
                        )

                )
        );
    }

    @Test
    public void testEqualsReversed() {
        this.checkNotEquals(
                SequenceParser.with(
                        Lists.of(
                                PARSER3,
                                PARSER2,
                                PARSER1
                        )

                )
        );
    }

    @Test
    public void testEqualsDifferentOptionalness() {
        this.checkNotEquals(
                SequenceParser.with(
                        Lists.of(
                                PARSER1.optional(),
                                PARSER2,
                                PARSER3
                        )

                )
        );
    }

    @Override
    public SequenceParser<ParserContext> createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createParser(),
                PARSER1 + ", " + PARSER2 + ", [" + PARSER3 + "]"
        );
    }

    @Test
    public void testToStringWrappedSequenceParser() {
        this.toStringAndCheck(
                SequenceParser.with(
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
                PARSER1 + ", " + PARSER2 + ", " + PARSER3 + ", " + PARSER4
        );
    }

    @Test
    public void testToStringWrappedSequenceParser2() {
        this.toStringAndCheck(
                SequenceParser.with(
                        Lists.of(
                                PARSER1,
                                SequenceParser.with(
                                        Lists.of(
                                                PARSER2,
                                                SequenceParser.with(
                                                        Lists.of(
                                                                PARSER3,
                                                                PARSER4
                                                        )
                                                )
                                        )
                                ),
                                parser("@@@")
                        )
                ),
                PARSER1 + ", " + PARSER2 + ", " + PARSER3 + ", " + PARSER4 + ", \"@@@\""
        );
    }

    @Test
    public void testToStringSurroundAlternativeParser() {
        this.toStringAndCheck(
                SequenceParser.with(
                        Lists.of(
                                PARSER1,
                                AlternativesParser.with(
                                        Lists.of(
                                                PARSER2,
                                                PARSER3
                                        )
                                ),
                                PARSER4
                        )
                ),
                PARSER1 + ", (" + PARSER2 + " | " + PARSER3 + "), " + PARSER4
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SequenceParser<ParserContext>> type() {
        return Cast.to(SequenceParser.class);
    }
}
