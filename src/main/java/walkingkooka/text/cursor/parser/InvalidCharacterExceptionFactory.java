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

import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.text.cursor.MaxPositionTextCursor;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;

import java.util.Objects;
import java.util.function.BiFunction;

// Invalid character '$' at 2
// Invalid character '$' at 2 expected XYZ
// Invalid character '$' at (1,2)
// Invalid character '$' at (1,2) expected XYZ

/**
 * An enum with numerous {@link InvalidCharacterException} factory {@link BiFunction} which may be given as a parameter to
 * {@link ParserContexts#basic(BiFunction, DateTimeContext, DecimalNumberContext)}.
 */
public enum InvalidCharacterExceptionFactory implements BiFunction<Parser<?>, TextCursor, InvalidCharacterException> {

    /**
     * <pre>
     * Invalid character '$' at 1
     * </pre>
     */
    POSITION,

    /**
     * <pre>
     * Invalid character '$' at (2,3)
     * </pre>
     */
    COLUMN_AND_LINE,

    /**
     * <pre>
     * Invalid character '$' at 4 expected X
     * </pre>
     */
    POSITION_EXPECTED,

    /**
     * <pre>
     * Invalid character '$' at (5,6) expected Y
     * </pre>
     */
    COLUMN_AND_LINE_EXPECTED;

    @Override
    public InvalidCharacterException apply(final Parser<?> parser,
                                           final TextCursor cursor) {
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(cursor, "cursor");

        final TextCursorLineInfo lineInfo = cursor.lineInfo();

        int position = cursor.isEmpty() ?
                0 :
                lineInfo.textOffset();
        if (cursor instanceof MaxPositionTextCursor) {
            final MaxPositionTextCursor max = (MaxPositionTextCursor) cursor;
            position = Math.max(
                    max.max(),
                    position
            );
        }

        InvalidCharacterException ice = new InvalidCharacterException(
                lineInfo.text()
                        .toString(), // text
                position
        );

        if (this == COLUMN_AND_LINE || this == COLUMN_AND_LINE_EXPECTED) {
            ice = ice.setColumnAndLine(
                    lineInfo.column(),
                    lineInfo.lineNumber()
            );
        }

        if (this == POSITION_EXPECTED || this == COLUMN_AND_LINE_EXPECTED) {
            ice = ice.appendToMessage("expected " + parser);
        }

        return ice;
    }
}
