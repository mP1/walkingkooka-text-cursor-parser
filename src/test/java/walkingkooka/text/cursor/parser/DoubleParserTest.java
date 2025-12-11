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
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import walkingkooka.Cast;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.text.cursor.TextCursor;

import java.math.MathContext;
import java.util.Locale;

strictfp
public final class DoubleParserTest extends NonEmptyParserTestCase<DoubleParser<ParserContext>, DoubleParserToken> {

    private final static char VALUE_SEPARATOR = ',';

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
        this.parseAndCheck2(
            "+0",
            +0.0
        );
    }

    @Test
    public void testParsePlusZero2() {
        this.parseAndCheck2(
            "+0",
            +0.0,
            "~"
        );
    }

    @Test
    public void testParseMinusZero() {
        this.parseAndCheck2(
            "-0",
            -0.0
        );
    }

    @Test
    public void testParseMinusZero2() {
        this.parseAndCheck2(
            "-0",
            -0.0,
            "~"
        );
    }

    @Test
    public void testParseZero() {
        this.parseAndCheck2(
            "0",
            0
        );
    }

    @Test
    public void testParseZero2() {
        this.parseAndCheck2(
            "0",
            0,
            "~"
        );
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
    public void testParsePlusZeroDecimal() {
        this.parseAndCheck2(
            "+0.",
            +0.
        );
    }

    @Test
    public void testParsePlusZeroDecimal2() {
        this.parseAndCheck2(
            "+0.",
            +0.,
            "~"
        );
    }

    @Test
    public void testParseMinusZeroDecimal() {
        this.parseAndCheck2(
            "-0.",
            -0.0
        );
    }

    @Test
    public void testParseMinusZeroDecimal2() {
        this.parseAndCheck2(
            "-0.",
            -0.0,
            "~"
        );
    }

    @Test
    public void testParseZeroDecimal() {
        this.parseAndCheck2(
            "0.",
            0
        );
    }

    @Test
    public void testParseZeroDecimal2() {
        this.parseAndCheck2(
            "0.",
            0,
            "~"
        );
    }

    @Test
    public void testParseZeroDecimalFraction() {
        this.parseAndCheck2(
            "0.5",
            0.5
        );
    }

    @Test
    public void testParseZeroDecimalFraction2() {
        this.parseAndCheck2(
            "0.5",
            0.5,
            "~"
        );
    }

    @Test
    public void testParseZeroDecimalFraction3() {
        this.parseAndCheck2(
            "0.875",
            0.875
        );
    }

    @Test
    public void testParseZeroDecimalFraction4() {
        this.parseAndCheck2(
            "0.875",
            0.875,
            "~"
        );
    }

    @Test
    public void testParseMinusZeroDecimalFraction() {
        this.parseAndCheck2(
            "-0.5",
            -0.5
        );
    }

    @Test
    public void testParseMinusZeroDecimalFraction2() {
        this.parseAndCheck2(
            "-0.5",
            -0.5,
            "~"
        );
    }

    @Test
    public void testParseMinusZeroDecimalFraction3() {
        this.parseAndCheck2(
            "-0.875",
            -0.875
        );
    }

    @Test
    public void testParseMinusZeroDecimalFraction4() {
        this.parseAndCheck2(
            "-0.875",
            -0.875,
            "~"
        );
    }

    @Test
    public void testParseZeroDecimalFraction5() {
        this.parseAndCheck2(
            "-0.000000001",
            -0.000000001
        );
    }

    @Test
    public void testParseNumber() {
        this.parseAndCheck2(
            "123",
            123
        );
    }

    @Test
    public void testParseNumber2() {
        this.parseAndCheck2(
            "123",
            123,
            "~"
        );
    }

    @Test
    public void testParseNumberDecimal() {
        this.parseAndCheck2(
            "123.",
            123
        );
    }

    @Test
    public void testParseNumberDecimal2() {
        this.parseAndCheck2(
            "123.",
            123,
            "~"
        );
    }

    @Test
    public void testParseNumberDecimalFraction() {
        this.parseAndCheck2(
            "123.5",
            123.5
        );
    }

    @Test
    public void testParseNumberDecimalFraction2() {
        this.parseAndCheck2(
            "123.5",
            123.5,
            "~"
        );
    }

    @Test
    public void testParseNumberDecimalFraction3() {
        this.parseAndCheck2(
            "123.875",
            123.875
        );
    }

    @Test
    public void testParseNumberDecimalFraction4() {
        this.parseAndCheck2(
            "123.875",
            123.875,
            "~"
        );
    }

    @Test
    public void testParseMinusNumberDecimal() {
        this.parseAndCheck2(
            "-123.",
            -123
        );
    }

    @Test
    public void testParseMinusNumberDecimal2() {
        this.parseAndCheck2(
            "-123.",
            -123,
            "~"
        );
    }

    @Test
    public void testParseMinusNumberDecimalFraction() {
        this.parseAndCheck2(
            "-123.5",
            -123.5
        );
    }

    @Test
    public void testParseMinusNumberDecimalFraction2() {
        this.parseAndCheck2(
            "-123.5",
            -123.5,
            "~"
        );
    }

    @Test
    public void testParseMinusNumberDecimalFraction3() {
        this.parseAndCheck2(
            "-123.875",
            -123.875
        );
    }

    @Test
    public void testParseMinusNumberDecimalFraction4() {
        this.parseAndCheck2(
            "-123.875",
            -123.875,
            "~"
        );
    }

    @Test
    public void testParseZeroDecimalZeroes() {
        this.parseAndCheck2(
            "0.0000",
            0.000
        );
    }

    @Test
    public void testParseMinusZeroDecimalZeroes() {
        this.parseAndCheck2(
            "-0.0000",
            -0.000
        );
    }

    @Test
    public void testParseZeroE() {
        this.parseAndCheck2(
            "0E",
            0
        );
    }

    @Test
    public void testParseZeroE2() {
        this.parseAndCheck2(
            "0E",
            0,
            "~"
        );
    }

    @Test
    public void testParseNumberE() {
        this.parseAndCheck2(
            "1E",
            1
        );
    }

    @Test
    public void testParseNumberE2() {
        this.parseAndCheck2(
            "1E",
            1,
            "~"
        );
    }

    @Test
    public void testParseNumberE3() {
        this.parseAndCheck2(
            "123E",
            123
        );
    }

    @Test
    public void testParseNumberE4() {
        this.parseAndCheck2(
            "123E",
            123,
            "~"
        );
    }

    @Test
    public void testParseNumberEExponent() {
        this.parseAndCheck2(
            "123E45",
            123E45
        );
    }

    @Test
    public void testParseNumberEExponent2() {
        this.parseAndCheck2(
            "123E45",
            123E45,
            "~"
        );
    }

    @Test
    public void testParseNumberEPlusExponent() {
        this.parseAndCheck2(
            "123E+45",
            123E+45
        );
    }

    @Test
    public void testParseNumberPlusEExponent2() {
        this.parseAndCheck2(
            "123E+45",
            123E+45,
            "~"
        );
    }

    @Test
    public void testParseNumberEMinusExponent() {
        this.parseAndCheck2(
            "123E-45",
            123E-45
        );
    }

    @Test
    public void testParseNumberMinusEExponent2() {
        this.parseAndCheck2(
            "123E-45",
            123E-45,
            "~"
        );
    }

    @Test
    public void tesMinustNumberEMinusExponent() {
        this.parseAndCheck2(
            "-123E-45",
            -123E-45
        );
    }

    @Test
    @DisabledIfSystemProperty(named = "github-actions", matches = "true")
    public void testParseMinusNumberMinusEExponent2() {
        this.parseAndCheck2(
            "-123E-45",
            -123E-45,
            "~"
        );
    }

    @Test
    @DisabledIfSystemProperty(named = "github-actions", matches = "true")
    public void testParseNumberDecimalFractionEExponent() {
        this.parseAndCheck2(
            "128.5E-67",
            128.5E-67
        );
    }

    @Test
    @DisabledIfSystemProperty(named = "github-actions", matches = "true")
    public void testParseNumberDecimalFractionEExponent2() {
        this.parseAndCheck2(
            "128.5E-67",
            128.5E-67,
            "~"
        );
    }

    @Test
    @DisabledIfSystemProperty(named = "github-actions", matches = "true")
    public void testParseNumberDecimalFractionEExponent3() {
        this.parseAndCheck2(
            "-128.5E-67",
            -128.5E-67
        );
    }

    @Test
    @DisabledIfSystemProperty(named = "github-actions", matches = "true")
    public void testParseNumberDecimalFractionEExponent4() {
        this.parseAndCheck2(
            "-128.5E-67",
            -128.5E-67,
            "~"
        );
    }

    @Test
    public void testParseZeroDecimalFractionEExponent() {
        this.parseAndCheck2(
            "0.00000E-67",
            0,
            "~"
        );
    }

    @Test
    public void testParseNegativeZeroDecimalFractionEExponent2() {
        this.parseAndCheck2(
            "-0.00000E-67",
            -0.0,
            "~"
        );
    }

    @Test
    public void testParseNumberMultiCharacterExponent() {
        this.parseAndCheck4(
            "123XYZ45",
            123E45
        );
    }

    @Test
    public void testParseNumberMultiCharacterExponentPlus() {
        this.parseAndCheck4(
            "123XYZP45",
            123E45
        );
    }

    @Test
    public void testParseNumberMultiCharacterExponentMinus() {
        this.parseAndCheck4(
            "123XYZM45",
            123E-45
        );
    }

    @Test
    public void testParseNumberMultiCharacterExponentZero() {
        this.parseAndCheck4(
            "123XYZ0",
            123E0
        );
    }

    @Test
    public void testParseNanFail() {
        this.parseFailAndCheck("N");
    }

    @Test
    public void testParseNanFail2() {
        this.parseFailAndCheck("N2");
    }

    @Test
    public void testParseNanFail3() {
        this.parseFailAndCheck("Na");
    }

    @Test
    public void testParseNanFail4() {
        this.parseFailAndCheck("Na2");
    }

    @Test
    public void testParseNan() {
        this.parseAndCheck2(
            "NaN",
            Double.NaN
        );
    }

    @Test
    public void testParseNan2() {
        this.parseAndCheck2(
            "NaN",
            Double.NaN,
            "~"
        );
    }

    @Test
    public void testParseNan3() {
        this.parseAndCheck2(
            "NaN",
            Double.NaN,
            "1"
        );
    }

    @Test
    public void testParseInfinityFail() {
        this.parseFailAndCheck("I");
    }

    @Test
    public void testParseInfinityFail2() {
        this.parseFailAndCheck("I2");
    }

    @Test
    public void testParseInfinityFail3() {
        this.parseFailAndCheck("In");
    }

    @Test
    public void testParseInfinityFail4() {
        this.parseFailAndCheck("Inf");
    }

    @Test
    public void testParseInfinityFail5() {
        this.parseFailAndCheck("Infi");
    }

    @Test
    public void testParseInfinityFail6() {
        this.parseFailAndCheck("Infin");
    }

    @Test
    public void testParseInfinityFail7() {
        this.parseFailAndCheck("Infini");
    }

    @Test
    public void testParseInfinityFail8() {
        this.parseFailAndCheck("Infinit");
    }

    @Test
    public void testParseInfinity() {
        this.parseAndCheck2(
            "Infinity",
            Double.POSITIVE_INFINITY
        );
    }

    @Test
    public void testParseInfinity2() {
        this.parseAndCheck2(
            "Infinity",
            Double.POSITIVE_INFINITY,
            "~"
        );
    }

    @Test
    public void testParsePlusInfinity() {
        this.parseAndCheck2(
            "+Infinity",
            Double.POSITIVE_INFINITY
        );
    }

    @Test
    public void testParseMinusInfinity() {
        this.parseAndCheck2(
            "-Infinity",
            Double.NEGATIVE_INFINITY
        );
    }

    @Test
    public void testParseDifferentDecimalSeparator() {
        this.parseAndCheck3(
            "1!25",
            1.25
        );
    }

    @Test
    public void testParseDifferentExponentSymbol() {
        this.parseAndCheck3(
            "5X2",
            Double.parseDouble("5E2")
        );
    }

    @Test
    public void testParseDifferentMinusSign() {
        this.parseAndCheck3(
            "M123",
            -123
        );
    }

    @Test
    public void testParseDifferentPlusSign() {
        this.parseAndCheck3(
            "P123",
            123
        );
    }

    private TextCursor parseAndCheck3(final String text,
                                      final double value) {
        return this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                VALUE_SEPARATOR,
                DateTimeContexts.fake(),
                new FakeDecimalNumberContext() {
                    @Override
                    public char decimalSeparator() {
                        return '!';
                    }

                    @Override
                    public String exponentSymbol() {
                        return "X";
                    }

                    @Override
                    public char negativeSign() {
                        return 'M';
                    }

                    @Override
                    public char positiveSign() {
                        return 'P';
                    }

                    @Override
                    public char zeroDigit() {
                        return '0';
                    }
                }
            ),
            text,
            ParserTokens.doubleParserToken(value, text),
            text,
            ""
        );
    }

    private TextCursor parseAndCheck4(final String text,
                                      final double value) {
        return this.parseAndCheck(
            this.createParser(),
            ParserContexts.basic(
                false, // canNumbersHaveGroupSeparator
                InvalidCharacterExceptionFactory.POSITION,
                VALUE_SEPARATOR,
                DateTimeContexts.fake(),
                new FakeDecimalNumberContext() {
                    @Override
                    public char decimalSeparator() {
                        return '!';
                    }

                    @Override
                    public String exponentSymbol() {
                        return "XYZ";
                    }

                    @Override
                    public char negativeSign() {
                        return 'M';
                    }

                    @Override
                    public char positiveSign() {
                        return 'P';
                    }

                    @Override
                    public char zeroDigit() {
                        return '0';
                    }
                }
            ),
            text,
            ParserTokens.doubleParserToken(value, text),
            text,
            ""
        );
    }

    @Override
    public DoubleParser<ParserContext> createParser() {
        return DoubleParser.instance();
    }

    @Override
    public ParserContext createContext() {
        return ParserContexts.basic(
            false, // canNumbersHaveGroupSeparator,
            InvalidCharacterExceptionFactory.POSITION,
            VALUE_SEPARATOR,
            this.dateTimeContext(),
            this.decimalNumberContext()
        );
    }

    private TextCursor parseAndCheck2(final String in,
                                      final double value) {
        return this.parseAndCheck2(
            in,
            value,
            ""
        );
    }

    private TextCursor parseAndCheck2(final String text,
                                      final double value,
                                      final String textAfter) {
        return this.parseAndCheck(
            text + textAfter,
            DoubleParserToken.with(
                value,
                text
            ),
            text,
            textAfter
        );
    }

    @Test
    public void testParseNonArabicDigits() {
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
                VALUE_SEPARATOR,
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(
                    DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
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
            ParserTokens.doubleParserToken(
                12.5,
                text
            ),
            text,
            ""
        );
    }

    // toString.........................................................................................................

    @Test
    public void testParseToString() {
        this.toStringAndCheck(
            this.createParser(),
            "Double"
        );
    }

    // type.............................................................................................................

    @Override
    public Class<DoubleParser<ParserContext>> type() {
        return Cast.to(DoubleParser.class);
    }
}
