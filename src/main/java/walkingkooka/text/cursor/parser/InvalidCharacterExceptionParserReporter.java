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
import walkingkooka.InvalidCharacterException;
import walkingkooka.text.cursor.MaxPositionTextCursor;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ParserReporter} that throws an {@link walkingkooka.InvalidCharacterException}.
 * <pre>
 * Invalid character 'X' at pos 123
 * </pre>
 * Note if the cursor is a {@link MaxPositionTextCursor} then that position will be compard against the current and the greater returned.
 */
final class InvalidCharacterExceptionParserReporter<C extends ParserContext> implements ParserReporter<C> {

    /**
     * Type safe getter.
     */
    static <C extends ParserContext> InvalidCharacterExceptionParserReporter<C> get() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static InvalidCharacterExceptionParserReporter<ParserContext> INSTANCE = new InvalidCharacterExceptionParserReporter<>();

    /**
     * Private ctor use singleton getter.
     */
    private InvalidCharacterExceptionParserReporter() {
        super();
    }

    @Override
    public Optional<ParserToken> report(final TextCursor cursor,
                                        final C context,
                                        final Parser<C> parser) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(parser, "parser");

        final TextCursorLineInfo lineInfo = cursor.lineInfo();

        throw new InvalidCharacterException(
                lineInfo.text()
                        .toString(),
                cursor.isEmpty() ?
                        0 : // if cursor is empty prolly best to complain with pos = 0
                        lineInfoTextOffsetOrMaxPositionTextCursor(
                                cursor,
                                lineInfo
                        )
        );
    }

    /**
     * Special cases if the {@link TextCursor} is a {@link MaxPositionTextCursor} return its max rather than 0.
     */
    private int lineInfoTextOffsetOrMaxPositionTextCursor(final TextCursor cursor,
                                                          final TextCursorLineInfo lineInfo) {
        int pos = lineInfo.textOffset();

        if (cursor instanceof MaxPositionTextCursor) {
            final MaxPositionTextCursor max = (MaxPositionTextCursor) cursor;
            pos = Math.max(
                    max.max(),
                    pos
            );
        }

        return pos;
    }

    @Override
    public String toString() {
        return "Fail";
    }
}
