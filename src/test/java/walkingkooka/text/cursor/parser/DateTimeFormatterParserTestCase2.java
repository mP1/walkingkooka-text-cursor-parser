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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;

import java.math.MathContext;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class DateTimeFormatterParserTestCase2<P extends DateTimeFormatterParser<ParserContext>, T extends ParserToken>
        extends DateTimeFormatterParserTestCase<P>
        implements ParserTesting2<P, ParserContext>,
        HashCodeEqualsDefinedTesting2<P> {

    DateTimeFormatterParserTestCase2() {
        super();
    }

    @Test
    public final void testWithNullFormatterFails() {
        assertThrows(NullPointerException.class, () -> this.createParser((Function<DateTimeContext, DateTimeFormatter>) null));
    }

    @Override
    public final P createParser() {
        return this.createParser(this.pattern());
    }

    final P createParser(final String pattern) {
        // JAPAN will be overridden by DateTimeFormatterParser
        return this.createParser(
                new TestFunction(pattern)
        );
    }

    // this function needs to implement hashCode/equals otherwise testEquals will fail.
    static class TestFunction implements Function<DateTimeContext, DateTimeFormatter> {

        TestFunction(final String pattern) {
            this.pattern = pattern;
        }

        @Override
        public DateTimeFormatter apply(final DateTimeContext context) {
            return DateTimeFormatter.ofPattern(this.pattern)
                    .withLocale(context.locale());
        }

        @Override
        public int hashCode() {
            return this.pattern.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof TestFunction && this.equals0((TestFunction) other);
        }

        private boolean equals0(final TestFunction other) {
            return this.pattern.equals(other.pattern);
        }

        private final String pattern;

        @Override
        public String toString() {
            return this.pattern;
        }
    }

    private P createParser(final DateTimeFormatter formatter) {
        return this.createParser((c) -> formatter.withLocale(c.locale()));
    }

    abstract P createParser(final Function<DateTimeContext, DateTimeFormatter> formatter);

    abstract T createParserToken(final DateTimeFormatter formatter, final String text);

    final DateTimeFormatter formatter() {
        return DateTimeFormatter.ofPattern(this.pattern());
    }

    abstract String pattern();

    @Override
    public ParserContext createContext() {
        return ParserContexts.basic(
                DateTimeContexts.locale(
                        LOCALE,
                        1900,
                        50,
                        LocalDateTime::now
                ),
                DecimalNumberContexts.decimalFormatSymbols(
                        new DecimalFormatSymbols(LOCALE),
                        '+',
                        LOCALE,
                        MathContext.UNLIMITED
                )
        );
    }

    private final static Locale LOCALE = Locale.ENGLISH;

    final void parseAndCheck2(final String pattern,
                              final String text) {
        this.parseAndCheck2(pattern, text, "");
    }

    final void parseAndCheck2(final String pattern,
                              final String text,
                              final String after) {
        final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(pattern).withLocale(LOCALE);
        formatter2.parse(text);

        this.parseAndCheck(this.createParser(formatter2),
                text + after,
                this.createParserToken(formatter2, text),
                text,
                after);
    }

    final TextCursor parseFailAndCheck2(final String pattern, final String cursorText) {
        return this.parseFailAndCheck(this.createParser(pattern), cursorText);
    }

    final void parseThrows2(final String pattern,
                            final String text,
                            final String expected) {
        final ParserException thrown = assertThrows(
                ParserException.class,
                () -> this.parse(
                        this.createParser(pattern),
                        TextCursors.charSequence(text),
                        this.createContext()
                )
        );

        final String thrownMessage = thrown.getMessage();

        this.checkEquals(
                true,
                thrownMessage.startsWith(expected),
                () -> "parse " + text
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public final void testEqualsDifferentFormatter() {
        this.checkNotEquals(
                this.createParser(
                        (c) -> {
                            throw new UnsupportedOperationException();
                        }
                ),
                this.createParser(
                        (c) -> {
                            throw new UnsupportedOperationException();
                        }
                )
        );
    }

    @Test
    public final void testEqualsDifferentToString() {
        this.checkNotEquals(
                this.createParser(),
                this.createParser().setToString("Different")
        );
    }

    @Override
    public P createObject() {
        return this.createParser();
    }
}
