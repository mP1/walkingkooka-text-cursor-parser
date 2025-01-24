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

import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.Optional;

/**
 * A {@link Parser} that matches a long number using a given radix. Note it does not require or match a leading prefix.
 * Note this only parses numeric digits and not any leading minus sign.
 */
final class LongParser<C extends ParserContext> extends NonEmptyParser<C> implements RequiredParser<C> {

    /**
     * Factory that creates a {@link LongParser}
     */
    static <C extends ParserContext> LongParser<C> with(final int radix) {
        if (radix <= 0) {
            throw new IllegalArgumentException("Invalid radix " + radix + " < 0");
        }

        return new LongParser<>(
                radix,
                10 == radix ? "Long" : "Long(base=" + radix + ")"
        );
    }

    /**
     * Private ctor to limit subclassing.
     */
    private LongParser(final int radix,
                       final String toString) {
        super(toString);
        this.radix = radix;
    }

    /**
     * Reads character by character until a non digit is found, using a {@link Long} to hold the value.
     */
    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint save) {
        final char negativeSign = context.negativeSign();
        final char positiveSign = context.positiveSign();

        LongParserToken token;

        final int radix = this.radix;
        long number = 0;
        boolean empty = true;
        boolean overflow = false;
        boolean signed = false;

        for (; ; ) {
            if (cursor.isEmpty()) {
                token = empty ?
                        null :
                        this.longParserToken(number, save);
                break;
            }

            char c = cursor.at();
            if (empty && 10 == radix) {
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
            final int digit = Character.digit(c, radix);
            if (-1 == digit) {
                token = empty ?
                        null :
                        this.longParserToken(number, save);
                break;
            }
            empty = false;

            try {
                number = Math.multiplyExact(number, radix);
                number = signed ?
                        Math.subtractExact(number, digit) :
                        Math.addExact(number, digit);
            } catch (final ArithmeticException cause) {
                overflow = true;
            }
            cursor.next();
        }

        if (overflow) {
            throw new ParserException("Number overflow " + CharSequences.quote(save.textBetween()));
        }

        return Optional.ofNullable(token);
    }

    private LongParserToken longParserToken(final Long value,
                                            final TextCursorSavePoint save) {
        return LongParserToken.with(
                value,
                save.textBetween()
                        .toString()
        );
    }

    private final int radix;

    // ParserSetToString..........................................................................................................

    @Override
    LongParser<C> replaceToString(final String toString) {
        return new LongParser<>(
                this.radix,
                toString
        );
    }
}
