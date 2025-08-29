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

import walkingkooka.text.Whitespace;
import walkingkooka.text.cursor.TextCursor;

import java.util.Objects;
import java.util.Optional;

/**
 * Wraps another {@link Parser} replacing or ignoring its {@link #toString()} with the provided {@link String}.
 */
final class CustomToStringParser<C extends ParserContext> extends ParserWrapper<C> {

    static <C extends ParserContext> Parser<C> wrap(final Parser<C> parser,
                                                    final String toString) {
        Objects.requireNonNull((Parser<?>) parser, "parser");
        Whitespace.failIfNullOrEmptyOrWhitespace(toString, "toString");

        Parser<C> parseWithToString;

        if (parser.toString().equals(toString)) {
            parseWithToString = parser;
        } else {
            Parser<C> wrap = parser;
            if (parser instanceof CustomToStringParser) {
                // unwrap then re-wrap the parser...
                final CustomToStringParser<C> custom = wrap.cast();
                wrap = custom.parser;
            }
            parseWithToString = new CustomToStringParser<>(wrap, toString);
        }

        return parseWithToString;
    }

    private CustomToStringParser(final Parser<C> parser,
                                 final String toString) {
        super(parser, toString);
    }

    // Parser..........................................................................................................

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final C context) {
        return this.parser.parse(
            cursor,
            context
        );
    }

    // ParserSetToString..........................................................................................................

    @Override
    Parser<C> replaceToString(final String toString) {
        return wrap(
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
