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

import walkingkooka.Cast;
import walkingkooka.EndOfTextException;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ParserReporter} that builds an error message that looks something like:
 * <pre>
 * Invalid character 'X' at ... expected A | B | C
 * </pre>
 */
final class BasicParserReporter<C extends ParserContext> implements ParserReporter<C> {

    /**
     * Type safe getter.
     */
    static <C extends ParserContext> BasicParserReporter<C> get() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static BasicParserReporter<ParserContext> INSTANCE = new BasicParserReporter<>();

    /**
     * Private ctor use singleton getter.
     */
    private BasicParserReporter() {
        super();
    }

    /**
     * Handles reporting either an END OF TEXT or INVALID CHARACTER or returning a {@link ParserToken}.
     * <pre>
     * End of text at 1, 1 expected ...
     * Invalid character at 1, 1 expected ...
     * </pre>
     */
    @Override
    public Optional<ParserToken> report(final TextCursor cursor,
                                        final C context,
                                        final Parser<C> parser) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(parser, "parser");

        final boolean endOfText = cursor.isEmpty();
        final TextCursorLineInfo info = cursor.lineInfo();

        if (endOfText) {
            final StringBuilder message = new StringBuilder();
            message.append("End of text");

            message.append(" at ");
            message.append(info.summary());

            message.append(" expected ");
            message.append(parser);

            throw new EndOfTextException(message.toString());
        } else {
            throw context.invalidCharacterException(
                    parser,
                    cursor
            );
        }
    }

    @Override
    public String toString() {
        return "Fail";
    }
}
