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

import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.cursor.parser.ParserContextDelegatorTest.TestParserContextDelegator;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

public final class ParserContextDelegatorTest implements ParserContextTesting<TestParserContextDelegator>,
    DecimalNumberContextDelegator {

    @Override
    public TestParserContextDelegator createContext() {
        return new TestParserContextDelegator();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    @Override
    public MathContext mathContext() {
        return DecimalNumberContextDelegator.super.mathContext();
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    static final class TestParserContextDelegator implements ParserContextDelegator {
        @Override
        public ParserContext parserContext() {
            final Locale locale = Locale.ENGLISH;

            return ParserContexts.basic(
                false, // isGroupSeparatorWithinNumbersSupported
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                    ),
                    locale,
                    1950, // defaultYear
                    50, // twoDigitYear
                    LocalDateTime::now
                ),
                DECIMAL_NUMBER_CONTEXT
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    // class............................................................................................................

    @Override
    public Class<TestParserContextDelegator> type() {
        return TestParserContextDelegator.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
