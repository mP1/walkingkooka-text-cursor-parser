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

public class BigIntegerParserTest extends NonEmptyParserTestCase<BigIntegerParser<ParserContext>, BigIntegerParserToken> {

    private final static int RADIX = 10;

    // with.............................................................................................................

    @Test
    public void testWithNegativeRadixFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> BigIntegerParser.with(-1)
        );
    }

    @Test
    public void testWithZeroRadixFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> BigIntegerParser.with(0)
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
    public void testParsePlusSignFail() {
        this.parseFailAndCheck("+");
    }

    @Test
    public void testParseMinusSignFail() {
        this.parseFailAndCheck("-");
    }

    @Test
    public void testParseZero() {
        this.parseAndCheck2(
            "0",
            0,
            "0",
            ""
        );
    }

    @Test
    public void testParseZeroZero() {
        this.parseAndCheck2(
            "00",
            0,
            "00",
            ""
        );
    }

    @Test
    public void testParseZeroZeroZero() {
        this.parseAndCheck2(
            "000",
            0,
            "000",
            ""
        );
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
            "12305",
            12305,
            "12305",
            ""
        );
    }

    @Test
    public void testParseZeroDecimal() {
        this.parseAndCheck2(
            "0123",
            123,
            "0123",
            ""
        );
    }

    @Test
    public void testParseZeroZeroDecimal() {
        this.parseAndCheck2(
            "00123",
            123,
            "00123",
            ""
        );
    }

    @Test
    public void testParsePlusSignDecimal() {
        this.parseAndCheck2(
            "+1",
            1,
            "+1",
            ""
        );
    }

    @Test
    public void testParsePlusSignDecimal2() {
        this.parseAndCheck2(
            "+0",
            0,
            "+0",
            ""
        );
    }

    @Test
    public void testParsePlusSignDecimal3() {
        this.parseAndCheck2(
            "+123",
            123,
            "+123",
            ""
        );
    }

    @Test
    public void testParseMinusSignDecimal() {
        this.parseAndCheck2(
            "-1",
            -1,
            "-1",
            ""
        );
    }

    @Test
    public void testParseMinusSignDecimal2() {
        this.parseAndCheck2(
            "-123",
            -123,
            "-123",
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
    public void testParseHex() {
        this.parseAndCheck3(
            16,
            "1234xyz",
            0x1234,
            "1234",
            "xyz"
        );
    }

    @SuppressWarnings("OctalInteger")
    @Test
    public void testParseOctal() {
        this.parseAndCheck3(
            8,
            "012345678xyz",
            01234567,
            "01234567",
            "8xyz"
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
            BigIntegerParser.with(16),
            text,
            ARABIC_ZERO_DIGIT,
            0xff
        );
    }

    @Test
    public void testParseRadix10NonArabicDigits() {
        this.parseAndCheck3(
            this.createParser(),
            arabicDigit(1) +
                arabicDigit(2),
            ARABIC_ZERO_DIGIT,
            12
        );
    }

    @Test
    public void testParseRadix10NonArabicDigits2() {
        this.parseAndCheck3(
            this.createParser(),
            "M" +
                arabicDigit(1) +
                arabicDigit(2),
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
                false, // canNumbersHaveGroupSeparator
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
            ParserTokens.bigInteger(
                BigInteger.valueOf(expected),
                text
            ),
            text,
            ""
        );
    }

    @Override
    public BigIntegerParser<ParserContext> createParser() {
        return BigIntegerParser.with(RADIX);
    }

    @Override
    public ParserContext createContext() {
        return ParserContexts.basic(
            false, // canNumbersHaveGroupSeparator
            InvalidCharacterExceptionFactory.POSITION,
            DateTimeContexts.fake(),
            this.decimalNumberContext()
        );
    }

    private TextCursor parseAndCheck2(final String in,
                                      final long value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck2(
            in,
            BigInteger.valueOf(value),
            text,
            textAfter
        );
    }

    private TextCursor parseAndCheck2(final String in,
                                      final BigInteger value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck(
            in,
            BigIntegerParserToken.with(
                value,
                text
            ),
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
            BigIntegerParser.with(radix),
            this.createContext(),
            TextCursors.charSequence(from),
            BigIntegerParserToken.with(
                BigInteger.valueOf(value),
                text
            ),
            text,
            textAfter
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createParser(),
            "BigInteger"
        );
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(
            BigIntegerParser.with(8),
            "BigInteger(base=8)"
        );
    }

    // type.............................................................................................................

    @Override
    public Class<BigIntegerParser<ParserContext>> type() {
        return Cast.to(BigIntegerParser.class);
    }
}
