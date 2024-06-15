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
    private final static Parser<ParserContext> PARSER1 = parser(TEXT1);
    private final static Parser<ParserContext> PARSER2 = parser(TEXT2);
    private final static Parser<ParserContext> PARSER3 = parser(TEXT3);
    private final static StringParserToken TOKEN1 = string(TEXT1);
    private final static StringParserToken TOKEN2 = string(TEXT2);
    private final static StringParserToken TOKEN3 = string(TEXT3);
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
        this.parseAndCheck(SequenceParserBuilder.empty()
                        .optional(PARSER3)
                        .required(PARSER2)
                        .required(PARSER1)
                        .build(),
                this.createContext(),
                text,
                ParserTokens.sequence(Lists.of(TOKEN2, TOKEN1), text),
                text);
    }

    @Test
    public void testParseAllOptionalFail() {
        this.parseFailAndCheck(SequenceParserBuilder.empty()
                        .optional(PARSER3)
                        .optional(PARSER2)
                        .optional(PARSER1)
                        .build(),
                "!@#");
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
        this.parseAndCheck(text, SEQUENCE_MISSING, text, "");
    }

    @Test
    public void testParseAllRequiredMissingOptional2() {
        final String text = TEXT1 + TEXT2;
        final String textAfter = "...";
        this.parseAndCheck(text + textAfter,
                SEQUENCE_MISSING,
                text,
                textAfter);
    }

    @Test
    public void testParseAllRequiredMissingOptional3() {
        final String text = TEXT1 + TEXT2;
        final String textAfter = "12";
        this.parseAndCheck(text + textAfter,
                SEQUENCE_MISSING,
                text,
                textAfter);
    }

    @Test
    public void testParseAllRequiredAndOptional() {
        final String text = TEXT1 + TEXT2 + TEXT3;
        this.parseAndCheck(text,
                SEQUENCE_TOKEN3,
                text,
                "");
    }

    @Test
    public void testParseAllRequiredAndOptional2() {
        final String text = TEXT1 + TEXT2 + TEXT3;
        final String textAfter = "...";
        this.parseAndCheck(text + textAfter,
                SEQUENCE_TOKEN3,
                text,
                textAfter);
    }

    @Override
    public SequenceParser<ParserContext> createParser() {
        return Cast.to(SequenceParserBuilder.empty()
                .required(PARSER1)
                .required(PARSER2)
                .optional(PARSER3)
                .build());
    }

    private static Parser<ParserContext> parser(final String string) {
        return Parsers.string(string, CaseSensitivity.SENSITIVE);
    }

    private static StringParserToken string(final String s) {
        return ParserTokens.string(s, s);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualWithoutNames() {
        this.checkEquals(SequenceParserBuilder.empty()
                .required(PARSER1)
                .required(PARSER2)
                .optional(PARSER3)
                .build());
    }

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(SequenceParserBuilder.empty()
                .required(PARSER3)
                .required(PARSER2)
                .required(PARSER1)
                .build());
    }

    @Test
    public void testEqualsDifferentRequiredOptionals() {
        this.checkNotEquals(SequenceParserBuilder.empty()
                .optional(PARSER1)
                .required(PARSER2)
                .required(PARSER3)
                .build());
    }

    @Test
    public void testEqualsBuiltUsingDefaultMethods() {
        this.checkEquals(PARSER1.builder()
                .required(PARSER2.cast())
                .optional(PARSER3.cast())
                .build());
    }

    @Override
    public SequenceParser<ParserContext> createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(),
                "(" + PARSER1 + ", " + PARSER2 + ", [" + PARSER3 + "])");
    }

    // Class............................................................................................................

    @Override
    public Class<SequenceParser<ParserContext>> type() {
        return Cast.to(SequenceParser.class);
    }
}
