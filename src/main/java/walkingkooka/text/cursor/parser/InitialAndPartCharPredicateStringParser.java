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

import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Parser} that uses two {@link walkingkooka.predicate.character.CharPredicate}.
 * The final matched token must have a length between min and max.
 */
final class InitialAndPartCharPredicateStringParser<C extends ParserContext> extends NonEmptyParser<C> {

    /**
     * Factory that creates a new {@link InitialAndPartCharPredicateStringParser}
     */
    static <C extends ParserContext> InitialAndPartCharPredicateStringParser<C> with(final CharPredicate initial,
                                                                                     final CharPredicate part,
                                                                                     final int minLength,
                                                                                     final int maxLength) {
        Objects.requireNonNull(initial, "initial");
        Objects.requireNonNull(part, "part");
        if (minLength < 1) {
            throw new IllegalArgumentException("Min length " + minLength + " must be greater than 0");
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException("Max length " + minLength + " must be greater than or equal min length " + minLength);
        }

        return new InitialAndPartCharPredicateStringParser<>(
                initial,
                part,
                minLength,
                maxLength,
                initial + " " + part + "{" + minLength + "," + maxLength + "}"
        );
    }

    /**
     * Private ctor use factory
     */
    private InitialAndPartCharPredicateStringParser(final CharPredicate initial,
                                                    final CharPredicate part,
                                                    final int minLength,
                                                    final int maxLength,
                                                    final String toString) {
        super(toString);

        this.initial = initial;
        this.part = part;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint start) {
        StringParserToken result = null;

        if (cursor.isNotEmpty()) {
            final char first = cursor.at();
            if (!this.initial.test(first)) {
                result = null;
            } else {
                final StringBuilder text = new StringBuilder();
                text.append(first);

                cursor.next();

                for (; ; ) {
                    if (cursor.isEmpty()) {
                        result = this.stringParserToken(text);
                        break;
                    }

                    final char at = cursor.at();
                    if (!this.part.test(at)) {
                        result = this.stringParserToken(text);
                        break;
                    }
                    text.append(at);
                    cursor.next();

                    // text too long...abort
                    if (text.length() >= this.maxLength) {
                        result = this.stringParserToken(text);
                        break;
                    }
                }
            }
        }

        return Optional.ofNullable(result);
    }

    private StringParserToken stringParserToken(final StringBuilder text) {
        return text.length() < this.minLength ?
                null :
                stringParserToken0(text);
    }

    private static StringParserToken stringParserToken0(final StringBuilder text) {
        final String finalText = text.toString();
        return StringParserToken.with(finalText, finalText);
    }

    private final CharPredicate initial;
    private final CharPredicate part;
    private final int minLength;
    private final int maxLength;


    // ParserSetToString..........................................................................................................

    @Override
    InitialAndPartCharPredicateStringParser<C> replaceToString(final String toString) {
        return new InitialAndPartCharPredicateStringParser<>(
                this.initial,
                this.part,
                this.minLength,
                this.maxLength,
                toString
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.initial,
                this.part,
                this.minLength,
                this.maxLength,
                this.toString
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof InitialAndPartCharPredicateStringParser && this.equals0((InitialAndPartCharPredicateStringParser<?>) other);
    }

    private boolean equals0(final InitialAndPartCharPredicateStringParser<?> other) {
        return this.initial.equals(other.initial) &&
                this.part.equals(other.part) &&
                this.minLength == other.minLength &&
                this.maxLength == other.maxLength &&
                this.toString.equals(other.toString);
    }
}
