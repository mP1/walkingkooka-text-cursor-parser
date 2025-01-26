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
final class CharPredicateStringParser<C extends ParserContext> extends NonEmptyParser<C>
        implements RequiredParser<C> {

    static <C extends ParserContext> CharPredicateStringParser<C> with(final CharPredicate predicate, final int minLength, final int maxLength) {
        Objects.requireNonNull(predicate, "predicate");
        if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid min length " + minLength + " <= 0");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("Invalid max length " + maxLength + " < min length " + minLength);
        }

        return new CharPredicateStringParser<>(
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

    private CharPredicateStringParser(final CharPredicate predicate,
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
        return this.predicate.test(cursor.at()) ? this.consumeRemaining(cursor, start) : Optional.empty();
    }

    private final CharPredicate predicate;

    private Optional<ParserToken> consumeRemaining(final TextCursor cursor, final TextCursorSavePoint start) {
        cursor.next();

        int i = 1;
        while (cursor.isNotEmpty() && i < this.maxLength && this.predicate.test(cursor.at())) {
            cursor.next();

            i++;
        }

        return i >= this.minLength ? stringParserToken(start) : Optional.empty();
    }

    private final int minLength;
    private final int maxLength;

    private static Optional<ParserToken> stringParserToken(final TextCursorSavePoint start) {
        final String text = start.textBetween().toString();
        return Optional.of(StringParserToken.with(text, text));
    }

    // ParserSetToString..........................................................................................................

    @Override
    CharPredicateStringParser<C> replaceToString(final String toString) {
        return new CharPredicateStringParser<>(
                this.predicate,
                this.minLength,
                this.maxLength,
                toString
        );
    }

    // Object...........................................................................................................

    @Override //
    int hashCode0() {
        return Objects.hash(
                this.predicate,
                this.minLength,
                this.maxLength
        );
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final CharPredicateStringParser<?> otherCharPredicateStringParser = other.cast();

        return this.predicate.equals(otherCharPredicateStringParser.predicate) &&
                this.minLength == otherCharPredicateStringParser.minLength &&
                this.maxLength == otherCharPredicateStringParser.maxLength &&
                this.toString.equals(otherCharPredicateStringParser.toString);
    }
}
