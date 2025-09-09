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
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.cursor.TextCursor;

import java.util.function.BiFunction;

/**
 * A collection of factory methods to create parsers.
 */
public final class ParserContexts implements PublicStaticHelper {

    /**
     * {@see BasicParserContext}
     */
    public static ParserContext basic(final boolean canNumbersHaveGroupSeparator,
                                      final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory,
                                      final DateTimeContext dateTimeContext,
                                      final DecimalNumberContext decimalNumberContext) {
        return BasicParserContext.with(
            canNumbersHaveGroupSeparator,
            invalidCharacterExceptionFactory,
            dateTimeContext,
            decimalNumberContext
        );
    }

    /**
     * {@see FakeParserContext}
     */
    public static ParserContext fake() {
        return new FakeParserContext();
    }

    /**
     * Stop creation.
     */
    private ParserContexts() {
        throw new UnsupportedOperationException();
    }
}
