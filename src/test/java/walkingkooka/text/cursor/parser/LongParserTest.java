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
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongParserTest extends NonEmptyParserTestCase<LongParser<ParserContext>, LongParserToken> {

    private final static int RADIX = 10;

    // with.............................................................................................................

    @Test
    public void testWithNegativeRadixFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> LongParser.with(-1)
        );
    }

    @Test
    public void testWithZeroRadixFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> LongParser.with(0)
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseFailure() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseFailure2() {
        this.parseFailAndCheck("abc");
    }

    @Test
    public void testParsePlusSignFails() {
        this.parseFailAndCheck("+");
    }

    @Test
    public void testParseMinusSignFails() {
        this.parseFailAndCheck("-");
    }

    @Test
    public void testParseDecimal() {
        this.parseAndCheck2(
                "1",
                1,
                "1",
                ""
        );
    }

    @Test
    public void testParseDecimal2() {
        this.parseAndCheck2(
                "123",
                123,
                "123",
                ""
        );
    }

    @Test
    public void testParseDecimal3() {
        this.parseAndCheck2(
                "+123",
                123,
                "+123",
                ""
        );
    }

    @Test
    public void testParseUntilNonDigit() {
        this.parseAndCheck2(
                "123abc",
                123,
                "123",
                "abc"
        );
    }

    @Test
    public void testParseNegativeDecimal() {
        this.parseAndCheck2(
                "-123",
                -123,
                "-123",
                ""
        );
    }

    @Test
    public void testParseNegativeDecimal2() {
        this.parseAndCheck2(
                "-123//",
                -123,
                "-123",
                "//"
        );
    }

    @Test
    public void testParseHex() {
        this.parseAndCheck3(
                16,
                "1234xyz",
                0x1234,
                "1234",
                "xyz"
        );
    }

    @Test
    public void testParseOctal() {
        this.parseAndCheck3(
                8,
                "012345678xyz",
                342391,
                "01234567",
                "8xyz"
        );
    }

    @Test
    public void testParseMaxValueHex() {
        this.parseAndCheck3(
                16,
                "7fffffffffffffff",
                Long.MAX_VALUE,
                "7fffffffffffffff",
                ""
        );
    }

    @Test
    public void testParseMaxValueHex2() {
        this.parseAndCheck3(
                16,
                "7fffffffffffffff///",
                Long.MAX_VALUE,
                "7fffffffffffffff",
                "///"
        );
    }

    @Test
    public void testParseLongMaxValue() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE);
        final String text = bigInteger.toString();
        this.parseAndCheck3(
                10,
                text,
                Long.MAX_VALUE,
                text,
                ""
        );
    }

    @Test
    public void testParseLongMaxValue2() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE);
        final String text = bigInteger.toString();
        final String after = "//";
        this.parseAndCheck3(
                10,
                text + after,
                Long.MAX_VALUE,
                text,
                after
        );
    }

    @Test
    public void testParsePlusSignLongMaxValue() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE);
        final String text = "+" + bigInteger;

        this.parseAndCheck3(
                10,
                text,
                Long.MAX_VALUE,
                text,
                ""
        );
    }

    @Test
    public void testParsePlusSignLongMaxValue2() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE);
        final String text = "+" + bigInteger;
        final String after = "//";

        this.parseAndCheck3(
                10,
                text + after,
                Long.MAX_VALUE,
                text,
                after
        );
    }

    @Test
    public void testParseLongMinValue() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MIN_VALUE);
        final String text = bigInteger.toString();
        final String after = "";
        this.parseAndCheck3(
                10, // radix
                text + after,
                Long.MIN_VALUE,
                text,
                after
        );
    }

    @Test
    public void testParseLongMinValue2() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MIN_VALUE);
        final String text = bigInteger.toString();
        final String after = "//";

        this.parseAndCheck3(
                10, // radix
                text + after,
                Long.MIN_VALUE,
                text,
                after
        );
    }

    @Test
    public void testParseGreaterMaxValueFails() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE)
                .add(BigInteger.ONE);

        assertThrows(
                ParserException.class,
                () -> this.parseFailAndCheck(
                        LongParser.with(10),
                        bigInteger.toString()
                )
        );
    }

    @Test
    public void testParseLessMinValueFails() {
        final BigInteger bigInteger = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);

        assertThrows(
                ParserException.class,
                () -> this.parseFailAndCheck(
                        LongParser.with(10),
                        bigInteger.toString()
                )
        );
    }

    @Test
    public void testParseGreaterMaxValueHexFails() {
        assertThrows(
                ParserException.class,
                () -> this.parseFailAndCheck(
                        LongParser.with(16),
                        "8fffffffffffffff"
                )
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

    @Test
    public void testParseRadix16IgnoresDecimalNumberContextZeroNonArabicDigits() {
        final String text = "ff";

        this.parseAndCheck3(
                LongParser.with(16),
                text,
                ARABIC_ZERO_DIGIT,
                0xff
        );
    }

    @Test
    public void testParseRadix10NonArabicDigits() {
        this.parseAndCheck3(
                this.createParser(),
                arabicDigit(1) + arabicDigit(2),
                ARABIC_ZERO_DIGIT,
                12
        );
    }

    @Test
    public void testParseRadix10NonArabicDigits2() {
        this.parseAndCheck3(
                this.createParser(),
                "M" + arabicDigit(1) + arabicDigit(2),
                ARABIC_ZERO_DIGIT,
                -12
        );
    }

    private TextCursor parseAndCheck3(final String text,
                                      final long expected) {
        return this.parseAndCheck3(
                this.createParser(),
                text,
                '0',
                expected
        );
    }

    private TextCursor parseAndCheck3(final Parser<ParserContext> parser,
                                      final String text,
                                      final char zeroDigit,
                                      final long expected) {
        return this.parseAndCheck(
                parser,
                ParserContexts.basic(
                        InvalidCharacterExceptionFactory.POSITION,
                        DateTimeContexts.fake(),
                        new FakeDecimalNumberContext() {
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
                                return zeroDigit;
                            }
                        }
                ),
                text,
                ParserTokens.longParserToken(
                        expected,
                        text
                ),
                text,
                ""
        );
    }

    @Override
    public LongParser<ParserContext> createParser() {
        return LongParser.with(RADIX);
    }

    @Override
    public ParserContext createContext() {
        return ParserContexts.basic(
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                this.decimalNumberContext()
        );
    }

    private TextCursor parseAndCheck2(final String in,
                                      final long value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck3(
                RADIX,
                in,
                value,
                text,
                textAfter
        );
    }

    private TextCursor parseAndCheck3(final int radix,
                                      final String from,
                                      final long value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck(
                LongParser.with(radix),
                this.createContext(),
                TextCursors.charSequence(from),
                LongParserToken.with(value, text),
                text,
                textAfter
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createParser(),
                "Long"
        );
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(
                LongParser.with(8),
                "Long(base=8)"
        );
    }

    // type.............................................................................................................

    @Override
    public Class<LongParser<ParserContext>> type() {
        return Cast.to(LongParser.class);
    }
}
