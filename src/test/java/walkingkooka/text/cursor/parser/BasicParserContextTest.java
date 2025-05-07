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
import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.TextCursor;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicParserContextTest implements ClassTesting2<BasicParserContext>,
        ParserContextTesting<BasicParserContext> {

    private final static BiFunction<Parser<?>, TextCursor, InvalidCharacterException> INVALID_CHARACTER_EXCEPTION_FACTORY =
            (final Parser<?> parser,
             final TextCursor cursor) -> new InvalidCharacterException(
                    cursor.lineInfo()
                            .text()
                            .toString(),
                    cursor.at()
            );

    private final static String CURRENCY = "$$";
    private final static char DECIMAL = 'D';
    private final static String EXPONENT = "XX";
    private final static char GROUPING = 'G';
    private final static char MINUS = 'M';
    private final static char PERCENTAGE = 'R';
    private final static char PLUS = 'P';

    private final static Locale LOCALE = Locale.ENGLISH;
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL32;

    @Test
    public void testWithNullInvalidCharacterExceptionFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicParserContext.with(
                        null,
                        DateTimeContexts.fake(),
                        DecimalNumberContexts.fake()
                )
        );
    }

    @Test
    public void testWithNullDateTimeContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicParserContext.with(
                        INVALID_CHARACTER_EXCEPTION_FACTORY,
                        null,
                        DecimalNumberContexts.fake()
                )
        );
    }

    @Test
    public void testWithNullDecimalNumberContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicParserContext.with(
                        INVALID_CHARACTER_EXCEPTION_FACTORY,
                        DateTimeContexts.fake(),
                        null
                )
        );
    }

    @Test
    public void testLocale() {
        this.hasLocaleAndCheck(
                this.createContext(),
                LOCALE
        );
    }

    @Test
    public void testMathContext2() {
        this.hasMathContextAndCheck(
                this.createContext(),
                MATH_CONTEXT
        );
    }

    @Override
    public void testInvalidCharacterExceptionWithNullParserFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicParserContext createContext() {
        return BasicParserContext.with(
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                this.dateTimeContext(),
                this.decimalNumberContext()
        );
    }

    private DateTimeContext dateTimeContext() {
        final Locale locale = Locale.ENGLISH;
        return DateTimeContexts.basic(
                DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                ),
                locale,
                1900,
                50,
                LocalDateTime::now
        );
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
                DecimalNumberSymbols.with(
                        MINUS,
                        PLUS,
                        CURRENCY,
                        DECIMAL,
                        EXPONENT,
                        GROUPING,
                        PERCENTAGE
                ),
                LOCALE,
                MATH_CONTEXT
        );
    }

    @Override
    public String currencySymbol() {
        return CURRENCY;
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL;
    }

    @Override
    public String exponentSymbol() {
        return EXPONENT;
    }

    @Override
    public char groupSeparator() {
        return GROUPING;
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public char negativeSign() {
        return MINUS;
    }

    @Override
    public char percentageSymbol() {
        return PERCENTAGE;
    }

    @Override
    public char positiveSign() {
        return PLUS;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                this.dateTimeContext() + " " + this.decimalNumberContext()
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicParserContext> type() {
        return BasicParserContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
