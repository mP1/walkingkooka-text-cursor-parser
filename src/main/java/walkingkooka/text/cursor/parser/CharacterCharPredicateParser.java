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
 * A {@link Parser} that matches a single character using the provided {@link CharPredicate}
 */
final class CharacterCharPredicateParser<C extends ParserContext> extends NonEmptyParser<C> {

    static <C extends ParserContext> CharacterCharPredicateParser<C> with(final CharPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new CharacterCharPredicateParser<>(
                predicate,
                predicate.toString()
        );
    }

    private CharacterCharPredicateParser(final CharPredicate predicate,
                                         final String toString) {
        super(toString);
        this.predicate = predicate;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint save) {
        final char first = cursor.at();
        return this.predicate.test(first) ? this.makeSuccessfulResultAndAdvance(first, cursor) : Optional.empty();
    }

    private final CharPredicate predicate;

    private Optional<ParserToken> makeSuccessfulResultAndAdvance(final char c, final TextCursor cursor) {
        final Optional<ParserToken> token = Optional.of(CharacterParserToken.with(c, String.valueOf(c)));
        cursor.next();
        return token;
    }

    // ParserSetToString..........................................................................................................

    @Override
    CharacterCharPredicateParser<C> replaceToString(final String toString) {
        return new CharacterCharPredicateParser<>(
                this.predicate,
                toString
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.predicate.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof CharacterCharPredicateParser && this.equals0((CharacterCharPredicateParser<?>) other);
    }

    private boolean equals0(final CharacterCharPredicateParser<?> other) {
        return this.predicate.equals(other.predicate);
    }
}
