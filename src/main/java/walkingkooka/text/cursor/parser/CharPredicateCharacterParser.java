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
final class CharPredicateCharacterParser<C extends ParserContext> extends NonEmptyParser<C>
        implements RequiredParser<C> {

    static <C extends ParserContext> CharPredicateCharacterParser<C> with(final CharPredicate predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new CharPredicateCharacterParser<>(
                predicate,
                predicate.toString()
        );
    }

    private CharPredicateCharacterParser(final CharPredicate predicate,
                                         final String toString) {
        super(toString);
        this.predicate = predicate;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint save) {
        final char first = cursor.at();
        return Optional.ofNullable(
                this.predicate.test(first) ?
                        this.characterParserTokenAndAdvance(
                                first,
                                cursor
                        ) :
                        null
        );
    }

    private final CharPredicate predicate;

    private ParserToken characterParserTokenAndAdvance(final char c,
                                                       final TextCursor cursor) {
        cursor.next();
        return CharacterParserToken.with(
                c,
                String.valueOf(c)
        );
    }

    // ParserSetToString..........................................................................................................

    @Override
    CharPredicateCharacterParser<C> replaceToString(final String toString) {
        return new CharPredicateCharacterParser<>(
                this.predicate,
                toString
        );
    }

    // Object...........................................................................................................

    @Override //
    int hashCode0() {
        return Objects.hash(
                this.predicate
        );
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final CharPredicateCharacterParser<?> otherCharPredicateCharacterParser = other.cast();

        return this.predicate.equals(otherCharPredicateCharacterParser.predicate);
    }
}
