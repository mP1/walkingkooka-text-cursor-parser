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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.cursor.TextCursor;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ParserTest implements ClassTesting<Parser<ParserContext>>,
        ParseStringTesting<ParserToken> {

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
                () -> new Parser<ParserContext>() {
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

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> new Parser<ParserContext>() {
                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final ParserContext context) {
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
    public void testParseTextConsumesSomeReturnsEmpty() {
        final String text = "ABC123";

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> new Parser<ParserContext>() {
                    @Override
                    public Optional<ParserToken> parse(final TextCursor cursor,
                                                       final ParserContext context) {
                        cursor.next();
                        return Optional.empty();
                    }
                }.parseText(
                        text,
                        ParserContexts.fake()
                )
        );
        this.checkEquals(
                new InvalidCharacterException(text, 1)
                        .getMessage(),
                thrown.getMessage()
        );
    }

    @Override
    public ParserToken parseString(final String text) {
        return Parsers.longParser(10)
                .parseText(
                        text,
                        ParserContexts.basic(
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
