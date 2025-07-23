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

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.util.BiFunctionTesting;

public final class InvalidCharacterExceptionFactoryTest implements BiFunctionTesting<InvalidCharacterExceptionFactory, Parser<?>, TextCursor, InvalidCharacterException> {

    @Test
    public void testPositionApply() {
        this.applyAndCheck(
                InvalidCharacterExceptionFactory.POSITION,
                Parsers.fake(),
                TextCursors.charSequence("xyz")
                        .next(),
                new InvalidCharacterException(
                        "xyz",
                        1
                )
        );
    }

    @Test
    public void testPositionApplyWhenCursorEmpty() {
        this.applyAndCheck(
                InvalidCharacterExceptionFactory.POSITION,
                Parsers.fake(),
                TextCursors.charSequence("xyz")
                        .end(),
                new InvalidCharacterException(
                        "xyz",
                        0
                )
        );
    }

    @Test
    public void testPositionApplyWhenMaxTextCursor() {
        final String text = "abcdef";

        final TextCursor cursor = TextCursors.maxPosition(
                TextCursors.charSequence(text)
        );

        cursor.next();

        final TextCursorSavePoint save = cursor.save();

        cursor.next();

        save.restore();

        this.applyAndCheck(
                InvalidCharacterExceptionFactory.POSITION,
                Parsers.fake(),
                cursor,
                new InvalidCharacterException(
                        text,
                        2
                )
        );
    }

    @Test
    public void testPositionApplyWhenMaxTextCursorEnd() {
        final String text = "abcdef";

        final TextCursor cursor = TextCursors.maxPosition(
                TextCursors.charSequence(text)
        );

        cursor.next();

        final TextCursorSavePoint save = cursor.save();

        cursor.end();

        save.restore();

        this.applyAndCheck(
                InvalidCharacterExceptionFactory.POSITION,
                Parsers.fake(),
                cursor,
                new InvalidCharacterException(
                        text,
                        text.length() - 1
                )
        );
    }

    @Test
    public void testColumnAndLineApply() {
        this.applyAndCheck(
                InvalidCharacterExceptionFactory.COLUMN_AND_LINE,
                Parsers.fake(),
                TextCursors.charSequence("xyz"),
                new InvalidCharacterException(
                        "xyz",
                        0
                ).setColumnAndLine(
                        1,
                        1
                )
        );
    }

    @Test
    public void testPositionAndExpectedApply() {
        this.applyAndCheck(
                InvalidCharacterExceptionFactory.POSITION_EXPECTED,
                new FakeParser() {
                    @Override
                    public String toString() {
                        return "PARSER123";
                    }
                },
                TextCursors.charSequence("xyz"),
                new InvalidCharacterException(
                        "xyz",
                        0
                ).appendToMessage("expected PARSER123")
        );
    }

    @Test
    public void testColumnAndLineAndExpectedApply() {
        this.applyAndCheck(
                InvalidCharacterExceptionFactory.COLUMN_AND_LINE_EXPECTED,
                new FakeParser() {
                    @Override
                    public String toString() {
                        return "PARSER456";
                    }
                },
                TextCursors.charSequence("xyz"),
                new InvalidCharacterException(
                        "xyz",
                        0
                ).setColumnAndLine(
                        1,
                        1
                ).appendToMessage("expected PARSER456")
        );
    }

    @Test
    public void testColumnAndLineAndExpectedApplyNotFirstLine() {
        final String text = "xyz\nabc\n";

        final TextCursor cursor = TextCursors.charSequence(text);
        cursor.next();
        cursor.next();
        cursor.next();
        cursor.next();
        cursor.next();

        this.applyAndCheck(
                InvalidCharacterExceptionFactory.COLUMN_AND_LINE_EXPECTED,
                new FakeParser() {
                    @Override
                    public String toString() {
                        return "PARSER456";
                    }
                },
                cursor,
                new InvalidCharacterException(
                        text,
                        5
                ).setColumnAndLine(
                        2,
                        2
                ).appendToMessage("expected PARSER456")
        );
    }

    @Override
    public InvalidCharacterExceptionFactory createBiFunction() {
        return InvalidCharacterExceptionFactory.POSITION;
    }

    @Override
    public Class<InvalidCharacterExceptionFactory> type() {
        return InvalidCharacterExceptionFactory.class;
    }
}
