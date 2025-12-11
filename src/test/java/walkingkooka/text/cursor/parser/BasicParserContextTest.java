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
import walkingkooka.math.DecimalNumberContextDelegator;
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
    ParserContextTesting<BasicParserContext>,
    DecimalNumberContextDelegator {

    private final static boolean IS_GROUP_SEPARATOR_WITHIN_NUMBERS_SUPPORTED = false;

    private final static BiFunction<Parser<?>, TextCursor, InvalidCharacterException> INVALID_CHARACTER_EXCEPTION_FACTORY =
        (final Parser<?> parser,
         final TextCursor cursor) -> new InvalidCharacterException(
            cursor.lineInfo()
                .text()
                .toString(),
            cursor.at()
        );

    private final static char VALUE_SEPARATOR = ',';

    private final static String CURRENCY = "$$";
    private final static char DECIMAL = '.';
    private final static char ZERO_DIGIT = '0';
    private final static String EXPONENT = "XX";
    private final static char GROUPING = ',';
    private final static String INFINITY = "INFINITY";
    private final static char MINUS = '-';
    private final static char MONETARY_DECIMAL = '.';
    private final static String NAN = "NAN";
    private final static char PERCENT = '%';
    private final static char PERMILL = '\u2030';
    private final static char PLUS = '+';

    private final static Locale LOCALE = Locale.ENGLISH;
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL32;

    @Test
    public void testWithNullInvalidCharacterExceptionFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicParserContext.with(
                IS_GROUP_SEPARATOR_WITHIN_NUMBERS_SUPPORTED,
                null,
                VALUE_SEPARATOR,
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
                IS_GROUP_SEPARATOR_WITHIN_NUMBERS_SUPPORTED,
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                VALUE_SEPARATOR,
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
                IS_GROUP_SEPARATOR_WITHIN_NUMBERS_SUPPORTED,
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                VALUE_SEPARATOR,
                DateTimeContexts.fake(),
                null
            )
        );
    }

    @Test
    public void testLocale() {
        this.localeAndCheck(
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
            IS_GROUP_SEPARATOR_WITHIN_NUMBERS_SUPPORTED,
            INVALID_CHARACTER_EXCEPTION_FACTORY,
            VALUE_SEPARATOR,
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

    @Override
    public int decimalNumberDigitCount() {
        return this.decimalNumberContext()
            .decimalNumberDigitCount();
    }


    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            DecimalNumberSymbols.with(
                MINUS,
                PLUS,
                ZERO_DIGIT,
                CURRENCY,
                DECIMAL,
                EXPONENT,
                GROUPING,
                INFINITY,
                MONETARY_DECIMAL,
                NAN,
                PERCENT,
                PERMILL
            ),
            LOCALE,
            MATH_CONTEXT
        );
    }

    @Override
    public MathContext mathContext() {
        return DecimalNumberContextDelegator.super.mathContext();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "canNumbersHaveGroupSeparator: false " +
                this.dateTimeContext() +
                " " +
                this.decimalNumberContext()
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
