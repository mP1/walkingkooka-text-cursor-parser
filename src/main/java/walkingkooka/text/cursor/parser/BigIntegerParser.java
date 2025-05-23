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

import java.math.BigInteger;
import java.util.Optional;

/**
 * A {@link Parser} that matches a number using a given radix. Note it does not require or match a leading prefix.
 * Note this only parses numeric digits and not any leading minus sign.
 */
final class BigIntegerParser<C extends ParserContext> extends NonEmptyParser<C>
        implements RequiredParser<C> {

    /**
     * Factory that creates a {@link BigIntegerParser}
     */
    static <C extends ParserContext> BigIntegerParser<C> with(final int radix) {
        if (radix <= 0) {
            throw new IllegalArgumentException("Invalid radix " + radix + " < 0");
        }

        return new BigIntegerParser<>(
                radix,
                10 == radix ?
                        "BigInteger" :
                        "BigInteger(base=" + radix + ")"
        );
    }

    /**
     * Private ctor to limit subclassing.
     */
    private BigIntegerParser(final int radix,
                             final String toString) {
        super(toString);

        this.radix = radix;
        this.radixBigInteger = BigInteger.valueOf(radix);
    }

    /**
     * Reads character by character until a non digit is found, using a {@link BigInteger} to hold the value.
     */
    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint save) {
        final char negativeSign = context.negativeSign();
        final char positiveSign = context.positiveSign();

        final int radix = this.radix;
        final boolean radix10 = 10 == radix;

        BigInteger number = BigInteger.ZERO;
        boolean empty = true;
        boolean signed = false;

        while (cursor.isNotEmpty()) {

            final char c = cursor.at();
            if (radix10 && empty) {
                if (negativeSign == c) {
                    signed = true;
                    cursor.next();
                    continue;
                }
                if (positiveSign == c) {
                    signed = false;
                    cursor.next();
                    continue;
                }
            }

            final int digit = radix10 ?
                    context.digit(c) :
                    Character.digit(c, radix);
            if (-1 == digit) {
                break;
            }
            empty = false;

            number = number.multiply(this.radixBigInteger);

            final BigInteger digitBigInteger = BigInteger.valueOf(digit);
            number = signed ?
                    number.subtract(digitBigInteger) :
                    number.add(digitBigInteger);

            cursor.next();
        }

        return Optional.ofNullable(
                empty ?
                        null :
                        ParserTokens.bigInteger(
                                number,
                                save.textBetween()
                                        .toString()
                        )
        );
    }

    private final int radix;
    private final BigInteger radixBigInteger;

    // ParserSetToString................................................................................................

    @Override
    BigIntegerParser<C> replaceToString(final String toString) {
        return new BigIntegerParser<>(
                this.radix,
                toString
        );
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
