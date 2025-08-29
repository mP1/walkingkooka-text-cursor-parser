/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;

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

@J2clTestInput(JunitTest.class)
public class JunitTest {

    @Test
    public void testBigIntegerParser() {
        final String text = "123";

        Assert.assertEquals(
            Optional.of(
                ParserTokens.bigInteger(
                    BigInteger.valueOf(123),
                    text
                )
            ),
            Parsers.bigInteger(10)
                .parse(
                    TextCursors.charSequence(text),
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
                                "Infinity",
                                '.',
                                "Nan",
                                '%',
                                '\u2030'
                            ),
                            Locale.forLanguageTag("en-AU"),
                            MathContext.DECIMAL32
                        )
                    )
                )
        );
    }
}
