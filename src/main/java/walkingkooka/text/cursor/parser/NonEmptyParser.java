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

import java.util.Optional;

/**
 * A template parser that only calls the abstract method if the cursor is not empty and also restores the cursor position,
 * on failures.
 */
abstract class NonEmptyParser<C extends ParserContext> extends ParserSetToString<C> {

    NonEmptyParser(final String toString) {
        super(toString);
    }

    @Override
    public final Optional<ParserToken> parse(final TextCursor cursor,
                                             final C context) {
        return cursor.isEmpty() ?
                this.empty() :
                this.prepareNonEmpty(cursor, context);
    }

    /**
     * Returns an empty optional which matches an unsuccessful parser attempt.
     */
    final Optional<ParserToken> empty() {
        return Optional.empty();
    }

    private Optional<ParserToken> prepareNonEmpty(final TextCursor cursor,
                                                  final C context) {
        final TextCursorSavePoint start = cursor.save();

        final Optional<ParserToken> result = this.tryParse(cursor, context, start);
        if (!result.isPresent()) {
            // unsuccessful restore cursor to original position...
            start.restore();
        }
        return result;
    }

    /**
     * This method is invoked with the first character and a {@link TextCursorSavePoint}.
     */
    abstract Optional<ParserToken> tryParse(final TextCursor cursor,
                                            final C context,
                                            final TextCursorSavePoint start);
}
