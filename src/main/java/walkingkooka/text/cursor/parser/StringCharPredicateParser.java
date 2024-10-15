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
 * A {@link Parser} that continues to consume characters that are matched by a given {@link CharPredicate}.
 */
final class StringCharPredicateParser<C extends ParserContext> extends NonEmptyParser<C> {

    static <C extends ParserContext> StringCharPredicateParser<C> with(final CharPredicate predicate, final int minLength, final int maxLength) {
        Objects.requireNonNull(predicate, "predicate");
        if (minLength <= 0) {
            throw new IllegalArgumentException("Min length " + minLength + " must be greater than 0");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("Maxlength " + maxLength + " must be greater/equal than minLength: " + minLength);
        }

        return new StringCharPredicateParser<>(
                predicate,
                minLength,
                maxLength,
                predicate.toString() +
                        '{' +
                        minLength +
                        ',' +
                        maxLength +
                        '}'
        );
    }

    private StringCharPredicateParser(final CharPredicate predicate,
                                      final int minLength,
                                      final int maxLength,
                                      final String toString) {
        super(toString);

        this.predicate = predicate;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint start) {
        return this.predicate.test(cursor.at()) ?
                this.consumeRemaining(cursor, start) :
                this.empty();
    }

    private final CharPredicate predicate;

    private Optional<ParserToken> consumeRemaining(final TextCursor cursor, final TextCursorSavePoint start) {
        cursor.next();

        int i = 1;
        while (cursor.isNotEmpty() && i < this.maxLength && this.predicate.test(cursor.at())) {
            cursor.next();

            i++;
        }

        return i >= this.minLength ?
                stringParserToken(start) :
                this.empty();
    }

    private final int minLength;
    private final int maxLength;

    private static Optional<ParserToken> stringParserToken(final TextCursorSavePoint start) {
        final String text = start.textBetween().toString();
        return Optional.of(StringParserToken.with(text, text));
    }

    // ParserSetToString..........................................................................................................

    @Override
    StringCharPredicateParser<C> replaceToString(final String toString) {
        return new StringCharPredicateParser<>(
                this.predicate,
                this.minLength,
                this.maxLength,
                toString
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.predicate,
                this.minLength,
                this.maxLength,
                this.toString
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof StringCharPredicateParser && this.equals0((StringCharPredicateParser<?>) other);
    }

    private boolean equals0(final StringCharPredicateParser<?> other) {
        return this.predicate.equals(other.predicate) &&
                this.minLength == other.minLength &&
                this.maxLength == other.maxLength &&
                this.toString.equals(other.toString);
    }
}
