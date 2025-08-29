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

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CustomToStringParserTest extends ParserWrapperTestCase<CustomToStringParser<ParserContext>>
    implements HashCodeEqualsDefinedTesting2<CustomToStringParser<ParserContext>> {

    private final static String STRING = "abc";
    private final static Parser<ParserContext> WRAPPED = Parsers.string(STRING, CaseSensitivity.SENSITIVE);
    private final static String CUSTOM_TO_STRING = "!!abc!!";

    // wrap.............................................................................................................

    @Test
    public void testWrapNullToStringFails() {
        assertThrows(
            NullPointerException.class,
            () -> CustomToStringParser.wrap(
                WRAPPED,
                null
            )
        );
    }

    @Test
    public void testWrapEmptyToStringFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> CustomToStringParser.wrap(
                WRAPPED,
                ""
            )
        );
    }

    @Test
    public void testWrapWhitespaceToStringFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> CustomToStringParser.wrap(
                WRAPPED,
                " \t"
            )
        );
    }

    @Test
    public void testWrapDoesntWrapEquivalentToString() {
        assertSame(
            WRAPPED,
            CustomToStringParser.wrap(
                WRAPPED,
                WRAPPED.toString()
            )
        );
    }

    @Test
    public void testWrapUnwrapOtherCustomToStringParser() {
        final Parser<ParserContext> first = CustomToStringParser.wrap(WRAPPED, "different");
        final CustomToStringParser<ParserContext> wrapped = Cast.to(CustomToStringParser.wrap(first, CUSTOM_TO_STRING));
        assertNotSame(first, wrapped);
        assertSame(WRAPPED, wrapped.parser, "wrapped parser");
        assertSame(CUSTOM_TO_STRING, wrapped.toString, "wrapped toString");
    }

    // setToString......................................................................................................

    @Test
    public void testSetToStringSame() {
        assertSame(
            WRAPPED,
            WRAPPED.setToString(
                WRAPPED.toString()
            )
        );
    }

    @Test
    public void testDefaultMethodSetToString() {
        final Parser<?> parser = WRAPPED.setToString(CUSTOM_TO_STRING);
        assertNotSame(WRAPPED, parser);
        this.checkEquals(
            CUSTOM_TO_STRING,
            parser.toString()
        );
    }

    @Test
    public void testDefaultMethodSetToStringCustomToString() {
        assertSame(
            CUSTOM_TO_STRING,
            this.createParser()
                .setToString(CUSTOM_TO_STRING)
                .toString()
        );
    }

    @Test
    public void testDefaultMethodSetToStringCustomToStringDifferent() {
        final String different = "2";
        final Parser<?> parser = this.createParser();
        final Parser<?> parser2 = parser.setToString(different);
        assertNotSame(parser, parser2);
        this.checkEquals(
            different,
            parser2.toString()
        );
    }

    // OR...............................................................................................................

    @Test
    @Override
    public void testOr() {
        final CustomToStringParser<ParserContext> parser1 = this.createParser().cast();
        final CustomToStringParser<ParserContext> parser2 = this.createParser().cast();
        this.checkEquals(Parsers.alternatives(Lists.of(parser1, parser2)), parser1.or(parser2));
    }

    @Override
    CustomToStringParser<ParserContext> createParser(final Parser<ParserContext> parser) {
        return CustomToStringParser.wrap(
            parser,
            CUSTOM_TO_STRING
        ).cast();
    }

    @Override
    Parser<ParserContext> wrappedParser() {
        return WRAPPED;
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
            this.createObject("different", CUSTOM_TO_STRING)
        );
    }

    @Test
    public void testEqualsDifferentParser2() {
        this.checkNotEquals(
            this.createObject("different2", CUSTOM_TO_STRING)
        );
    }

    @Test
    public void testEqualsDifferentCustomToString() {
        this.checkNotEquals(
            CustomToStringParser.wrap(WRAPPED, "different")
        );
    }

    @Override
    public CustomToStringParser<ParserContext> createObject() {
        return this.createParser();
    }

    private CustomToStringParser<ParserContext> createObject(final String parserText,
                                                             final String customToString) {
        return CustomToStringParser.wrap(Parsers.string(parserText, CaseSensitivity.SENSITIVE),
            customToString).cast();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), CUSTOM_TO_STRING);
    }

    // class............................................................................................................

    @Override
    public Class<CustomToStringParser<ParserContext>> type() {
        return Cast.to(CustomToStringParser.class);
    }
}
