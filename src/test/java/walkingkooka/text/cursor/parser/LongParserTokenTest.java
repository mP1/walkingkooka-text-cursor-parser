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

import static org.junit.jupiter.api.Assertions.assertSame;

public final class LongParserTokenTest extends ValueParserTokenTestCase<LongParserToken, Long> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final LongParserToken token = this.createToken();

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
            protected void visit(final LongParserToken t) {
                assertSame(token, t);
                b.append("3");
            }
        }.accept(token);
        this.checkEquals("132", b.toString());
    }

    @Test
    public void testIgnoresPrefix() {
        LongParserToken.with(123, "+123");
    }

    @Test
    public void testHex() {
        LongParserToken.with(0x1234, "0x1234");
    }

    @Override
    public LongParserToken createToken(final String text) {
        return LongParserToken.with(null != text ?
                Long.parseLong(text) :
                null, text);
    }

    @Override
    public String text() {
        return "123";
    }

    @Override
    public LongParserToken createDifferentToken() {
        return this.createToken("789");
    }

    @Override
    public Class<LongParserToken> type() {
        return LongParserToken.class;
    }
}
