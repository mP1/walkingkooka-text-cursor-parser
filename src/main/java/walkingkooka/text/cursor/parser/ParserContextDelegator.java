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
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.text.cursor.TextCursor;

import java.util.Locale;

public interface ParserContextDelegator extends ParserContext,
    DateTimeContextDelegator,
    DecimalNumberContextDelegator {

    @Override
    default DateTimeContext dateTimeContext() {
        return this.parserContext();
    }

    @Override
    default DecimalNumberContext decimalNumberContext() {
        return this.parserContext();
    }

    @Override
    default boolean canNumbersHaveGroupSeparator() {
        return this.parserContext()
            .canNumbersHaveGroupSeparator();
    }

    @Override
    default InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                final TextCursor cursor) {
        return this.parserContext()
            .invalidCharacterException(
                parser,
                cursor
            );
    }

    @Override
    default Locale locale() {
        return this.parserContext().locale();
    }

    ParserContext parserContext();
}
