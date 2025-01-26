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

import java.util.Objects;
import java.util.Optional;

/**
 * Decorates another {@link Parser} adding a post condition that the {@link ParserToken} is only returned if
 * the {@link TextCursor} is also empty.
 */
final class AndEmptyTextCursorParser<C extends ParserContext> extends ParserWrapper<C> {

    static <C extends ParserContext> AndEmptyTextCursorParser<C> with(final Parser<C> parser) {
        Objects.requireNonNull(parser, "parser");

        return parser instanceof AndEmptyTextCursorParser ?
                parser.cast() :
                new AndEmptyTextCursorParser<>(
                        parser,
                        parser.toString()
                );
    }

    private AndEmptyTextCursorParser(final Parser<C> parser,
                                     final String toString) {
        super(parser, toString);
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final C context) {
        final TextCursorSavePoint save = cursor.save();
        Optional<ParserToken> token = this.parser.parse(cursor, context);
        if (token.isPresent() && cursor.isNotEmpty()) {
            save.restore();
            token = Optional.empty();
        }
        return token;
    }

    @Override
    public Parser<C> andEmptyTextCursor() {
        return this;
    }

    // ParserSetToString..........................................................................................................

    @Override
    AndEmptyTextCursorParser<C> replaceToString(final String toString) {
        return new AndEmptyTextCursorParser<>(
                this.parser,
                toString
        );
    }

    // Object...........................................................................................................

    @Override //
    int hashCode1() {
        return 0;
    }

    @Override //
    boolean equalsParserWrapper(final ParserWrapper<?> other) {
        return true; // no new properties
    }
}
