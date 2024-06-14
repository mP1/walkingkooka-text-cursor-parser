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
import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.text.cursor.MaxPositionTextCursor;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class InvalidCharacterExceptionParserReporterTest implements ParserReporterTesting<InvalidCharacterExceptionParserReporter<FakeParserContext>, FakeParserContext> {

    @Test
    public void testNotEndOfText() {
        final String text = "Hello";

        final TextCursor cursor = TextCursors.charSequence(text);
        cursor.next();
        cursor.next();

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> this.report(cursor, this.createContext(), Parsers.fake())
        );

        this.checkEquals(
                new InvalidCharacterException(text, 2).getMessage(),
                thrown.getMessage()
        );
    }

    @Test
    public void testNotEmptyWhenMaxPositionTextCursor() {
        final String text = "Hello";

        final MaxPositionTextCursor cursor = TextCursors.maxPosition(
                TextCursors.charSequence(text)
        );

        final TextCursorSavePoint save = cursor.save();

        cursor.next();
        cursor.next();

        save.restore();

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> this.report(
                        cursor,
                        this.createContext(),
                        Parsers.fake()
                )
        );

        this.checkEquals(
                new InvalidCharacterException(text, 2).getMessage(),
                thrown.getMessage()
        );
    }

    @Test
    public void testEndOfText() {
        final String text = "Hello";

        final TextCursor cursor = TextCursors.charSequence(text);
        cursor.end();

        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> this.report(cursor, this.createContext(), Parsers.fake())
        );

        this.checkEquals(
                new InvalidCharacterException(text, 0).getMessage(),
                thrown.getMessage()
        );
    }

    @Override
    public InvalidCharacterExceptionParserReporter<FakeParserContext> createParserReporter() {
        return InvalidCharacterExceptionParserReporter.get();
    }

    @Override
    public FakeParserContext createContext() {
        return new FakeParserContext();
    }

    @Override
    public Class<InvalidCharacterExceptionParserReporter<FakeParserContext>> type() {
        return Cast.to(InvalidCharacterExceptionParserReporter.class);
    }
}
