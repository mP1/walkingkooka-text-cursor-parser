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

import walkingkooka.datetime.DateTimeContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

/**
 * A {@link Parser} that uses a {@link DateTimeFormatter} to parse a {@link LocalDate}.
 */
final class DateTimeFormatterParserLocalDate<C extends ParserContext> extends DateTimeFormatterParser<C> {

    static <C extends ParserContext> DateTimeFormatterParserLocalDate<C> with(final Function<DateTimeContext, DateTimeFormatter> formatter) {
        return new DateTimeFormatterParserLocalDate<>(
                check(formatter),
                formatter.toString()
        );
    }

    private DateTimeFormatterParserLocalDate(final Function<DateTimeContext, DateTimeFormatter> formatter,
                                             final String toString) {
        super(
                formatter,
                toString
        );
    }

    @Override
    LocalDateParserToken createParserToken(final TemporalAccessor value, final String text) {
        return ParserTokens.localDate(LocalDate.from(value), text);
    }

    // ParserSetToString..........................................................................................................

    @Override
    DateTimeFormatterParserLocalDate<C> replaceToString(final String toString) {
        return new DateTimeFormatterParserLocalDate<>(
                this.formatter,
                toString
        );
    }
}
