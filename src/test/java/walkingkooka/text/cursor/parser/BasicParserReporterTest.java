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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;

public final class BasicParserReporterTest implements ClassTesting2<BasicParserReporter<FakeParserContext>>,
    ParserReporterTesting<BasicParserReporter<FakeParserContext>, FakeParserContext> {

    @Test
    public void testReport() {
        // has a dependency on the results of TextCursorLineInfo methods...
        this.reportAndCheck(
            "abc def ghi",
            Parsers.fake()
                .setToString("ABC")
                .cast(),
            "Invalid character 'l' at 2"
        );
    }

    @Test
    public void testReportEmptyTextCursor() {
        final TextCursor cursor = TextCursors.charSequence("abc");
        cursor.end();
        this.reportAndCheck(
            cursor,
            Parsers.fake()
                .setToString("XYZ")
                .cast(),
            "End of text, expected XYZ"
        );
    }

    @Override
    public BasicParserReporter<FakeParserContext> createParserReporter() {
        return BasicParserReporter.get();
    }

    @Override
    public FakeParserContext createContext() {
        return new FakeParserContext() {
            @Override
            public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                       final TextCursor cursor) {
                return new InvalidCharacterException(
                    "Hello",
                    2
                );
            }
        };
    }

    @Override
    public Class<BasicParserReporter<FakeParserContext>> type() {
        return Cast.to(BasicParserReporter.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
