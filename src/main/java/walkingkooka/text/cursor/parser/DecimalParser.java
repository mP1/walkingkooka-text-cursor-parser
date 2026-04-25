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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

/**
 * A {@link Parser} that parses decimal numbers, starting with an optional sign, digits, separator and decimals.
 * Exponents, NAN and Infinity are not supported, returning a {@link }
 */
final class DecimalParser<C extends ParserContext> extends NonEmptyParser<C>
    implements RequiredParser<C> {

    /**
     * Factory that creates a {@link DecimalParser}
     */
    static <C extends ParserContext> DecimalParser<C> instance() {
        return new DecimalParser<>("Decimal");
    }

    /**
     * Private ctor to limit subclassing.
     */
    private DecimalParser(final String toString) {
        super(toString);
    }

    private final static int RADIX = 10;
    private final static BigDecimal RADIX_BIGDECIMAL = BigDecimal.valueOf(RADIX);

    private final static int NUMBER_SIGN = 1;
    private final static int NUMBER_ZERO = NUMBER_SIGN * 2;
    private final static int NUMBER_DIGIT = NUMBER_ZERO * 2;
    private final static int DECIMAL = NUMBER_DIGIT * 2;
    private final static int DECIMAL_DIGIT = DECIMAL * 2;

    private final static int FINISH = DECIMAL_DIGIT * 2;

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint save) {
        final char decimalSeparator = context.decimalSeparator();
        final int negativeSign = context.negativeSign();
        final int positiveSign = context.positiveSign();
        final char zeroDigit = context.zeroDigit();

        final MathContext mathContext = context.mathContext();

        DecimalParserToken token = null;

        // optional(+/-)
        // 0 OR 1-9
        //      repeat(0-9)
        // if DECIMAL
        //    repeat(0-9)

        BigDecimal number = BigDecimal.ZERO;
        boolean numberNegative = false;
        int fractionFactor = 0;

        int mode = NUMBER_SIGN | NUMBER_ZERO | NUMBER_DIGIT;
        boolean empty = true;

        for (; ; ) {
            final char c = cursor.at();

            for (; ; ) {
                if ((NUMBER_SIGN & mode) != 0) {
                    if (positiveSign == c) {
                        cursor.next();
                        mode = NUMBER_ZERO | NUMBER_DIGIT;
                        break;
                    }
                    if (negativeSign == c) {
                        cursor.next();
                        numberNegative = true;
                        mode = NUMBER_ZERO | NUMBER_DIGIT;
                        break;
                    }
                }
                if ((NUMBER_ZERO & mode) != 0) {
                    if (zeroDigit == c) {
                        cursor.next();
                        mode = NUMBER_ZERO | NUMBER_DIGIT | DECIMAL;
                        empty = false;
                        break;
                    }
                }
                if ((NUMBER_DIGIT & mode) != 0) {
                    final int digit = context.digit(c);
                    if (digit >= 0) {
                        cursor.next();
                        number = number(number, digit, mathContext);
                        mode = NUMBER_DIGIT | DECIMAL;
                        empty = false;
                        break;
                    }
                }
                if ((DECIMAL & mode) != 0) {
                    if (decimalSeparator == c) {
                        cursor.next();
                        mode = DECIMAL_DIGIT;
                        break;
                    }
                }
                if ((DECIMAL_DIGIT & mode) != 0) {
                    final int digit = context.digit(c);
                    if (digit >= 0) {
                        cursor.next();
                        number = number(
                            number,
                            digit,
                            mathContext
                        );
                        fractionFactor--;
                        break;
                    }
                }
                // invalid char
                mode = FINISH;
                break;
            }

            if (FINISH == mode || cursor.isEmpty()) {
                if (false == empty) {
                    if (numberNegative) {
                        number = number.negate(mathContext);
                    }
                    number = number.scaleByPowerOfTen(fractionFactor);
                    token = token(
                        number,
                        save
                    );
                }
                break;
            }
        }

        return Optional.ofNullable(token);
    }

    private static BigDecimal number(final BigDecimal value,
                                     final int digit,
                                     final MathContext context) {
        return value.multiply(
            RADIX_BIGDECIMAL,
            context
        ).add(
            BigDecimal.valueOf(digit)
        );
    }

    private static DecimalParserToken token(final BigDecimal value,
                                            final TextCursorSavePoint save) {
        return DecimalParserToken.with(
            value,
            save.textBetween()
                .toString()
        );
    }

    // ParserSetToString................................................................................................

    @Override
    DecimalParser<C> replaceToString(final String toString) {
        return new DecimalParser<>(toString);
    }

    // Object...........................................................................................................

    @Override //
    int hashCode0() {
        return 0;
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        return true; // no extra properties
    }
}
