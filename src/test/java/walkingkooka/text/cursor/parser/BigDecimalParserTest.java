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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.text.cursor.TextCursor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

public final class BigDecimalParserTest extends NonEmptyParserTestCase<BigDecimalParser<ParserContext>, BigDecimalParserToken> {

    @Test
    public void testParseFailure() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseFailure2() {
        this.parseFailAndCheck("abc");
    }

    @Test
    public void testParsePlusZero() {
        this.parseAndCheck2("+0");
    }

    @Test
    public void testParsePlusZero2() {
        this.parseAndCheck2("+0", "~");
    }

    @Test
    public void testParseMinusZero() {
        this.parseAndCheck2("-0");
    }

    @Test
    public void testParseMinusZero2() {
        this.parseAndCheck2("-0", "~");
    }

    @Test
    public void testParseZero() {
        this.parseAndCheck2("0");
    }

    @Test
    public void testParseZeroZeroZero() {
        this.parseAndCheck2("000");
    }

    @Test
    public void testParseZero2() {
        this.parseAndCheck2("0", "~");
    }

    @Test
    public void testParsePlusPlusFail() {
        this.parseFailAndCheck("++1");
    }

    @Test
    public void testParseMinusMinusFail() {
        this.parseFailAndCheck("--1");
    }

    @Test
    public void testParsePlusMinusFail() {
        this.parseFailAndCheck("+-1");
    }

    @Test
    public void testParseMinusPlusFail() {
        this.parseFailAndCheck("-+1");
    }

    @Test
    public void testParseGroupSeparatorWhenCanNumbersHaveGroupSeparatorFalseFails() {
        this.parseAndCheck2(
            "12",
            BigDecimal.valueOf(12),
            ",34"
        );
    }

    @Test
    public void testParsePlusZeroDecimal() {
        this.parseAndCheck2("+0.");
    }

    @Test
    public void testParsePlusZeroDecimal2() {
        this.parseAndCheck2("+0.", "~");
    }

    @Test
    public void testParseMinusZeroDecimal() {
        this.parseAndCheck2("-0.");
    }

    @Test
    public void testParseMinusZeroDecimal2() {
        this.parseAndCheck2("-0.", "~");
    }

    @Test
    public void testParseZeroDecimal() {
        this.parseAndCheck2("0.");
    }

    @Test
    public void testParseZeroDecimal2() {
        this.parseAndCheck2("0.", "~");
    }

    // group-separator not consumed.

    @Test
    public void testParseZeroDecimalFractionGroupSeparatorWhenCanNumbersHaveGroupSeparatorFalse() {
        this.parseAndCheck(
            this.createParser(),
            this.createContext(false), // canNumbersHaveGroupSeparator
            "0.5,",
            ParserTokens.bigDecimal(
                BigDecimal.valueOf(0.5),
                "0.5"
            ),
            "0.5",
            ","
        );
    }

    @Test
    public void testParseZeroDecimalFractionGroupSeparatorWhenCanNumbersHaveGroupSeparatorTrue() {
        this.parseAndCheck(
            this.createParser(),
            this.createContext(true), // canNumbersHaveGroupSeparator
            "0.5,",
            ParserTokens.bigDecimal(
                BigDecimal.valueOf(0.5), "0.5"
            ),
            "0.5",
            ","
        );
    }

    @Test
    public void testParseZeroDecimalFraction() {
        this.parseAndCheck2("0.5");
    }

    @Test
    public void testParseZeroDecimalFraction2() {
        this.parseAndCheck2("0.5", "~");
    }

    @Test
    public void testParseZeroDecimalFraction3() {
        this.parseAndCheck2("0.875");
    }

    @Test
    public void testParseZeroDecimalFraction4() {
        this.parseAndCheck2("0.875", "~");
    }

    @Test
    public void testParseMinusZeroDecimalFraction() {
        this.parseAndCheck2("-0.5");
    }

    @Test
    public void testParseMinusZeroDecimalFraction2() {
        this.parseAndCheck2("-0.5", "~");
    }

    @Test
    public void testParseMinusZeroDecimalFraction3() {
        this.parseAndCheck2("-0.875");
    }

    @Test
    public void testParseMinusZeroDecimalFraction4() {
        this.parseAndCheck2("-0.875", "~");
    }

    @Test
    public void testParseZeroDecimalFraction5() {
        this.parseAndCheck2("-0.000000001");
    }

    @Test
    public void testParseZeroNumber() {
        this.parseAndCheck2("0123");
    }

    @Test
    public void testParseZeroNumber2() {
        this.parseAndCheck2("00123");
    }

    @Test
    public void testParseNumber() {
        this.parseAndCheck2("123");
    }

    @Test
    public void testParseNumber2() {
        this.parseAndCheck2("123", "~");
    }

    @Test
    public void testParseNumberDecimal() {
        this.parseAndCheck2("123.");
    }

    @Test
    public void testParseNumberDecimal2() {
        this.parseAndCheck2("123.", "~");
    }

    @Test
    public void testParseNumberDecimalFraction() {
        this.parseAndCheck2("123.5");
    }

    @Test
    public void testParseNumberDecimalFraction2() {
        this.parseAndCheck2("123.5", "~");
    }

    @Test
    public void testParseNumberDecimalFraction3() {
        this.parseAndCheck2("123.875");
    }

    @Test
    public void testParseNumberDecimalFraction4() {
        this.parseAndCheck2("123.875", "~");
    }

    @Test
    public void testParseMinusNumberDecimal() {
        this.parseAndCheck2("-123.");
    }

    @Test
    public void testParseMinusNumberDecimal2() {
        this.parseAndCheck2("-123.", "~");
    }

    @Test
    public void testParseMinusNumberDecimalFraction() {
        this.parseAndCheck2("-123.5");
    }

    @Test
    public void testParseMinusNumberDecimalFraction2() {
        this.parseAndCheck2("-123.5", "~");
    }

    @Test
    public void testParseMinusNumberDecimalFraction3() {
        this.parseAndCheck2("-123.875");
    }

    @Test
    public void testParseMinusNumberDecimalFraction4() {
        this.parseAndCheck2("-123.875", "#");
    }

    @Test
    public void testParseZeroDecimalZeroes() {
        this.parseAndCheck2("0.0000");
    }

    @Test
    public void testParseMinusZeroDecimalZeroes() {
        this.parseAndCheck2("-0.0000");
    }

    @Test
    public void testParseZeroGroupSeparatorZeroWhenCanNumbersHaveGroupSeparatorTrue() {
        final String text = "1,234";

        this.parseAndCheck(
            this.createParser(),
            this.createContext(true), // CanNumbersHaveGroupSeparator=true
            text,
            ParserTokens.bigDecimal(
                BigDecimal.valueOf(1234),
                text
            ),
            text
        );
    }

    @Test
    public void testParseZeroE() {
        this.parseAndCheck2("0E", 0);
    }

    @Test
    public void testParseZeroE2() {
        this.parseAndCheck2("0E", 0, "~");
    }

    @Test
    public void testParseNumberE() {
        this.parseAndCheck2("1E", 1);
    }

    @Test
    public void testParseNumberE2() {
        this.parseAndCheck2("1E", 1, "~");
    }

    @Test
    public void testParseNumberE3() {
        this.parseAndCheck2("123E", 123);
    }

    @Test
    public void testParseNumberE4() {
        this.parseAndCheck2("123E", 123, "~");
    }

    @Test
    public void testParseNumberEExponent() {
        this.parseAndCheck2("123E45");
    }

    @Test
    public void testParseNumberEExponent2() {
        this.parseAndCheck2("123E45", "~");
    }

    @Test
    public void testParseNumberEPlusExponent() {
        this.parseAndCheck2("123E+45");
    }

    @Test
    public void testParseNumberPlusEExponent2() {
        this.parseAndCheck2("123E+45", "~");
    }

    @Test
    public void testParseNumberEMinusExponent() {
        this.parseAndCheck2("123E-45");
    }

    @Test
    public void testParseNumberMinusEExponent2() {
        this.parseAndCheck2("123E-45", "~");
    }

    @Test
    public void tesMinusNumberEMinusExponent() {
        this.parseAndCheck2("-123E-45");
    }

    @Test
    public void testParseMinusNumberMinusEExponent2() {
        this.parseAndCheck2("-123E-45", "~");
    }

    @Test
    public void testParseNumberDecimalFractionEExponent() {
        this.parseAndCheck2("123.5E-67");
    }

    @Test
    public void testParseNumberDecimalFractionEExponent2() {
        this.parseAndCheck2("123.5E-67", "~");
    }

    @Test
    public void testParseNumberDecimalFractionEExponent3() {
        this.parseAndCheck2("-123.5E-67");
    }

    @Test
    public void testParseNumberDecimalFractionEExponent4() {
        this.parseAndCheck2("-123.5E-67", "~");
    }

    @Test
    public void testParseZeroDecimalFractionEExponent() {
        this.parseAndCheck2("0.00000E-67", "~");
    }

    @Test
    public void testParseNegativeZeroDecimalFractionEExponent2() {
        this.parseAndCheck2("-0.00000E-67", "~");
    }

    @Test
    public void testParseNumberMultiCharacterExponent() {
        this.parseAndCheck4("123XYZ45", new BigDecimal("123E45"));
    }

    @Test
    public void testParseNumberMultiCharacterExponentPlus() {
        this.parseAndCheck4("123XYZ-45", new BigDecimal("123E+45"));
    }

    @Test
    public void testParseNumberMultiCharacterExponentMinus() {
        this.parseAndCheck4("123XYZ+45", new BigDecimal("123E-45"));
    }

    @Test
    public void testParseNumberMultiCharacterExponentZero() {
        this.parseAndCheck4("123XYZ0", new BigDecimal("123E0"));
    }

    @Test
    public void testParseNaNFails() {
        this.parseFailAndCheck("NaN");
    }

    @Test
    public void testParseInfinityFails() {
        this.parseFailAndCheck("Infinity");
    }

    @Test
    public void testParsePlusInfinityFails() {
        this.parseFailAndCheck("+Infinity");
    }

    @Test
    public void testParseMinusInfinityFails() {
        this.parseFailAndCheck("-Infinity");
    }

    @Test
    public void testParseDifferentDecimalSeparator() {
        this.parseAndCheck3("1*25", BigDecimal.valueOf(1.25));
    }

    @Test
    public void testParseDifferentExponentSymbol() {
        this.parseAndCheck3("5X2", new BigDecimal("5E2"));
    }

    @Test
    public void testParseDifferentMinusSign() {
        this.parseAndCheck3("+123", BigDecimal.valueOf(-123));
    }

    @Test
    public void testParseDifferentPlusSign() {
        this.parseAndCheck3("-123", BigDecimal.valueOf(123));
    }

    private TextCursor parseAndCheck3(final String text,
                                      final BigDecimal value) {
        return this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '+', // negativeSign
                        '-', // positiveSign
                        '0', // zero
                        "C", // currency
                        '*', // decimalPoint
                        "X", // exponentSymbol
                        '/', // groupSeparator
                        "Infinity", // infinity
                        '*', // monetary decimal separator
                        "NaN",
                        '$', // percent
                        '^' // permill
                    ),
                    Locale.ENGLISH,
                    MathContext.DECIMAL32
                )
            ),
            text,
            ParserTokens.bigDecimal(value, text),
            text,
            ""
        );
    }

    private TextCursor parseAndCheck4(final String text,
                                      final BigDecimal value) {
        return this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '+', // negativeSign
                        '-', // positiveSign
                        '0', // zeroDigit
                        "C", // currency
                        '*', // decimalPoint
                        "XYZ", // exponentSymbol
                        '/', // groupSeparator
                        "INFINITY",
                        '#', // monetaryDecimal
                        "NAN",
                        '$', // percent
                        '^' // permill
                    ),
                    Locale.ENGLISH,
                    MathContext.DECIMAL32
                )
            ),
            text,
            ParserTokens.bigDecimal(value, text),
            text,
            ""
        );
    }

    @Test
    public void testParseZeroWithNonArabicDigits() {
        final char zero = ARABIC_ZERO_DIGIT;

        final String text = "" + zero;

        this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '+', // negativeSign
                        '-', // positiveSign
                        zero, // zeroDigit
                        "C", // currency
                        '*', // decimalPoint
                        "XYZ", // exponentSymbol
                        '/', // groupSeparator
                        "INFINITY",
                        '#', // monetaryDecimal
                        "NAN",
                        '$', // percent
                        '^' // permill
                    ),
                    Locale.ENGLISH,
                    MathContext.DECIMAL32
                )
            ),
            text,
            ParserTokens.bigDecimal(
                BigDecimal.ZERO,
                text
            ),
            text,
            ""
        );
    }

    @Test
    public void testParseNumberExponentZeroWithNonArabicDigits() {
        final char zero = ARABIC_ZERO_DIGIT;

        final String text = new StringBuilder()
            .append((char) (zero + 1))
            .append((char) (zero + 2))
            .append("XYZ")
            .append(zero)
            .toString();

        this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '+', // negativeSign
                        '-', // positiveSign
                        zero, // zeroDigit
                        "C", // currency
                        '*', // decimalPoint
                        "XYZ", // exponentSymbol
                        '/', // groupSeparator
                        "INFINITY",
                        '#', // monetaryDecimal
                        "NAN",
                        '$', // percent
                        '^' // permill
                    ),
                    Locale.ENGLISH,
                    MathContext.DECIMAL32
                )
            ),
            text,
            ParserTokens.bigDecimal(
                BigDecimal.valueOf(12),
                text
            ),
            text,
            ""
        );
    }

    @Test
    public void testParseDecimalNumberWithNonArabicDigits() {
        final char zero = ARABIC_ZERO_DIGIT;

        final String text = new StringBuilder()
            .append((char) (zero + 1))
            .append((char) (zero + 2))
            .append('*')
            .append((char) (zero + 5))
            .toString();

        this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '+', // negativeSign
                        '-', // positiveSign
                        zero, // zeroDigit
                        "C", // currency
                        '*', // decimalPoint
                        "XYZ", // exponentSymbol
                        '/', // groupSeparator
                        "INFINITY",
                        '#', // monetaryDecimal
                        "NAN",
                        '$', // percent
                        '^' // permill
                    ),
                    Locale.ENGLISH,
                    MathContext.DECIMAL32
                )
            ),
            text,
            ParserTokens.bigDecimal(
                BigDecimal.valueOf(12.5),
                text
            ),
            text,
            ""
        );
    }

    @Override
    public BigDecimalParser<ParserContext> createParser() {
        return BigDecimalParser.with();
    }

    @Override
    public ParserContext createContext() {
        return this.createContext(
            false // canNumbersHaveGroupSeparator
        );
    }

    private ParserContext createContext(final boolean canNumbersHaveGroupSeparator) {
        return ParserContexts.basic(
            canNumbersHaveGroupSeparator,
            InvalidCharacterExceptionFactory.POSITION,
            DateTimeContexts.fake(),
            this.decimalNumberContext()
        );
    }

    private TextCursor parseAndCheck2(final String text) {
        return this.parseAndCheck2(text, "");
    }

    private TextCursor parseAndCheck2(final String text,
                                      final long value) {
        return this.parseAndCheck2(
            text,
            BigDecimal.valueOf(value),
            ""
        );
    }

    private TextCursor parseAndCheck2(final String text,
                                      final String textAfter) {
        return this.parseAndCheck2(
            text,
            new BigDecimal(text),
            textAfter
        );
    }

    private TextCursor parseAndCheck2(final String text,
                                      final long value,
                                      final String textAfter) {
        return this.parseAndCheck2(
            text,
            BigDecimal.valueOf(value),
            textAfter
        );
    }

    private TextCursor parseAndCheck2(final String text,
                                      final BigDecimal value,
                                      final String textAfter) {
        return this.parseAndCheck(
            text + textAfter,
            ParserTokens.bigDecimal(value, text),
            text,
            textAfter
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), "Decimal");
    }

    // class............................................................................................................

    @Override
    public Class<BigDecimalParser<ParserContext>> type() {
        return Cast.to(BigDecimalParser.class);
    }
}
