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

package walkingkooka.text.cursor.parser.sample;

import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sample {

    public static void main(final String[] args) {
        final String text = "123";

        assertEquals(
                Optional.of(
                        ParserTokens.bigInteger(
                                BigInteger.valueOf(123),
                                text
                        )
                ),
                Parsers.bigInteger(10)
                        .parse(TextCursors.charSequence(text),
                                ParserContexts.basic(
                                        InvalidCharacterExceptionFactory.POSITION,
                                        DateTimeContexts.fake(),
                                        DecimalNumberContexts.basic(
                                                DecimalNumberSymbols.with(
                                                        '-',
                                                        '+',
                                                        '0',
                                                        "$",
                                                        '.',
                                                        "E",
                                                        ',',
                                                        '%'
                                                ),
                                                Locale.forLanguageTag("en-AU"),
                                                MathContext.DECIMAL32
                                        )
                                )
                        )
        );
    }
}
