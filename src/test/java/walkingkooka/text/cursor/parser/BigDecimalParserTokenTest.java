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
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BigDecimalParserTokenTest extends ValueParserTokenTestCase<BigDecimalParserToken, BigDecimal> {

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> BigDecimalParserToken.with(null, "123"));
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final BigDecimalParserToken token = this.createToken();

        new FakeParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("2");
            }

            @Override
            protected void visit(final BigDecimalParserToken t) {
                assertSame(token, t);
                b.append("3");
            }
        }.accept(token);
        this.checkEquals("132", b.toString());
    }

    @Test
    public void testIgnoresPrefix() {
        BigDecimalParserToken.with(BigDecimal.valueOf(123), "+123");
    }

    @Test
    public void testWithFractions() {
        BigDecimalParserToken.with(BigDecimal.valueOf(12.5), "12.5 nonsense");
    }

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                ParserTokens.bigDecimal(
                        BigDecimal.TEN,
                        "different-text"
                ),
                "BigDecimal \"different-text\" 10 (java.math.BigDecimal)\n"
        );
    }

    @Test
    public void testTreePrintExponential() {
        this.treePrintAndCheck(
                BigDecimalParserToken.with(
                        new BigDecimal("1E2"),
                        "different-text"
                ),
                "BigDecimal \"different-text\" 100 (java.math.BigDecimal)\n"
        );
    }

    @Override
    public BigDecimalParserToken createToken(final String text) {
        return BigDecimalParserToken.with(new BigDecimal(text), text);
    }

    @Override
    public String text() {
        return "123";
    }

    @Override
    public BigDecimalParserToken createDifferentToken() {
        return BigDecimalParserToken.with(BigDecimal.valueOf(987), "987");
    }

    @Override
    public Class<BigDecimalParserToken> type() {
        return BigDecimalParserToken.class;
    }
}
