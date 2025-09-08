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
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * An adaptor for {@link DecimalNumberContext} to {@link ParserContext}.
 */
final class BasicParserContext implements ParserContext,
    DateTimeContextDelegator,
    DecimalNumberContextDelegator {

    /**
     * Creates a new {@link BasicParserContext}.
     */
    static BasicParserContext with(final boolean isGroupSeparatorWithinNumbersSupported,
                                   final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory,
                                   final DateTimeContext dateTimeContext,
                                   final DecimalNumberContext decimalNumberContext) {
        Objects.requireNonNull(invalidCharacterExceptionFactory, "invalidCharacterExceptionFactory");
        Objects.requireNonNull(dateTimeContext, "dateTimeContext");
        Objects.requireNonNull(decimalNumberContext, "decimalNumberContext");

        return new BasicParserContext(
            isGroupSeparatorWithinNumbersSupported,
            invalidCharacterExceptionFactory,
            dateTimeContext,
            decimalNumberContext
        );
    }

    /**
     * Private ctor use factory
     */
    private BasicParserContext(final boolean isGroupSeparatorWithinNumbersSupported,
                               final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory,
                               final DateTimeContext dateTimeContext,
                               final DecimalNumberContext decimalNumberContext) {
        super();

        this.isGroupSeparatorWithinNumbersSupported = isGroupSeparatorWithinNumbersSupported;
        this.invalidCharacterExceptionFactory = invalidCharacterExceptionFactory;
        this.dateTimeContext = dateTimeContext;
        this.decimalNumberContext = decimalNumberContext;
    }

    @Override
    public boolean isGroupSeparatorWithinNumbersSupported() {
        return this.isGroupSeparatorWithinNumbersSupported;
    }

    private final boolean isGroupSeparatorWithinNumbersSupported;

    @Override
    public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                               final TextCursor cursor) {
        return this.invalidCharacterExceptionFactory.apply(
            parser,
            cursor
        );
    }

    private final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory;

    @Override
    public Locale locale() {
        return this.dateTimeContext.locale();
    }

    // DateTimeContextDelegator.........................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        return this.dateTimeContext;
    }

    private final DateTimeContext dateTimeContext;

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return this.decimalNumberContext;
    }

    private final DecimalNumberContext decimalNumberContext;

    @Override
    public String toString() {
        return "isGroupSeparatorWithinNumbersSupported: " + isGroupSeparatorWithinNumbersSupported +
            " " +
            this.dateTimeContext +
            " " +
            this.decimalNumberContext;
    }
}