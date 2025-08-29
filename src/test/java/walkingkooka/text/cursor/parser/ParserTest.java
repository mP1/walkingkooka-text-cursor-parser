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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ParserTest implements ClassTesting<Parser<ParserContext>>,
    ParseStringTesting<ParserToken>,
    ParserTesting {

    // parseText........................................................................................................

    @Test
    public void testParseTextWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.parseString(null)
        );
    }

    @Test
    public void testParseTextWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> Parsers.longParser(10)
                .parseText(
                    "1",
                    null
                )
        );
    }

    @Test
    public void testParseTextInvalidCharacterFails() {
        final String text = "1X2";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                1
            )
        );
    }

    @Test
    public void testParseTextThrownException() {
        final String text = "'un-closed";

        assertThrows(
            IllegalArgumentException.class,
            () -> Parsers.singleQuoted().parseText(
                text,
                ParserContexts.fake()
            )
        );
    }

    @Test
    public void testParseTextConsumesTextReturnsEmpty() {
        final String text = "ABC123";

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> new FakeParser<>() {

                @Override
                public Optional<ParserToken> parse(final TextCursor cursor,
                                                   final ParserContext context) {
                    cursor.end();
                    return Optional.empty();
                }
            }.parseText(
                text,
                ParserContexts.fake()
            )
        );

        this.checkEquals(
            new InvalidCharacterException(
                text,
                0
            ).getMessage(),
            thrown.getMessage()
        );
    }

    @Test
    public void testParseTextReturnsEmpty() {
        final String text = "ABC123";

        final InvalidCharacterException ice = new InvalidCharacterException(
            "Hello",
            2
        );

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> new FakeParser<>() {

                @Override
                public Optional<ParserToken> parse(final TextCursor cursor,
                                                   final ParserContext context) {
                    return Optional.empty();
                }

                @Override
                public String toString() {
                    return "PARSER123";
                }
            }.parseText(
                text,
                new FakeParserContext() {
                    @Override
                    public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                               final TextCursor cursor) {
                        return ice;
                    }
                }
            )
        );
        assertSame(
            ice,
            thrown
        );
    }

    @Test
    public void testParseTextConsumesSomeReturnsEmpty() {
        final String text = "ABC123";

        final InvalidCharacterException ice = new InvalidCharacterException(
            "Hello",
            2
        );

        assertSame(
            ice,
            assertThrows(
                InvalidCharacterException.class,
                () -> new FakeParser<>() {

                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final ParserContext context) {
                        cursor.next();
                        return Optional.empty();
                    }

                    @Override
                    public String toString() {
                        return "PARSER123";
                    }
                }.parseText(
                    text,
                    new FakeParserContext() {

                        @Override
                        public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                                   final TextCursor cursor) {
                            return ice;
                        }
                    }
                )
            )
        );
    }

    @Override
    public ParserToken parseString(final String text) {
        return Parsers.longParser(10)
            .parseText(
                text,
                ParserContexts.basic(
                    InvalidCharacterExceptionFactory.POSITION,
                    DateTimeContexts.fake(),
                    DecimalNumberContexts.american(MathContext.DECIMAL32)
                )
            );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> cause) {
        return cause;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // and..............................................................................................................

    @Test
    public void testAndRequired() {
        final Parser<ParserContext> parser = Parsers.string("AAA", CaseSensitivity.SENSITIVE);
        final Parser<ParserContext> and = Parsers.string("BBB", CaseSensitivity.SENSITIVE);

        this.andAndCheck(
            parser,
            and,
            Parsers.sequence(
                Lists.of(
                    parser,
                    and
                )
            )
        );
    }

    // optional.........................................................................................................

    @Test
    public void testOptionalWhenRequired() {
        final Parser<ParserContext> required = Parsers.string("Hello", CaseSensitivity.SENSITIVE);

        this.optionalAndCheck(
            required,
            Parsers.repeating(
                0,
                1,
                required
            )
        );
    }

    @Test
    public void testOptionalWhenOptional() {
        final Parser<ParserContext> required = Parsers.string("Hello", CaseSensitivity.SENSITIVE);

        this.optionalAndCheck(
            required.optional(),
            Parsers.repeating(
                0,
                1,
                required
            )
        );
    }

    // required.........................................................................................................

    @Test
    public void testRequiredWhenOptional() {
        final Parser<ParserContext> required = Parsers.string("Hello", CaseSensitivity.SENSITIVE);
        final Parser<ParserContext> optional = required.optional();

        this.isOptionalAndCheck(
            optional,
            true
        );

        this.requiredAndCheck(
            optional,
            Parsers.repeating(
                0,
                1,
                required
            )
        );
    }

    @Test
    public void testRequiredWhenRequired() {
        final Parser<ParserContext> required = Parsers.string("Hello", CaseSensitivity.SENSITIVE);

        this.requiredAndCheck(
            required,
            required
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<Parser<ParserContext>> type() {
        return Cast.to(Parser.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
