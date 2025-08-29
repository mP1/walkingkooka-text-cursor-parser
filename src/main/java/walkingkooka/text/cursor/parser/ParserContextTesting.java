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
import walkingkooka.math.DecimalNumberContextTesting2;
import walkingkooka.text.cursor.TextCursors;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixing testing interface for {@link ParserContext}
 */
public interface ParserContextTesting<C extends ParserContext> extends DecimalNumberContextTesting2<C> {

    @Test
    default void testInvalidCharacterExceptionWithNullParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .invalidCharacterException(
                    null,
                    TextCursors.fake()
                )
        );
    }

    @Test
    default void testInvalidCharacterExceptionWithNullTextCursorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .invalidCharacterException(
                    Parsers.fake(),
                    null
                )
        );
    }

    @Override
    default String typeNameSuffix() {
        return ParserContext.class.getSimpleName();
    }
}
