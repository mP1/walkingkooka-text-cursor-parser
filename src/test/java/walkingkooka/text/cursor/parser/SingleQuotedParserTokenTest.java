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
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SingleQuotedParserTokenTest extends ValueParserTokenTestCase<SingleQuotedParserToken, String> {

    @Test
    public void testWithNullContentFails() {
        assertThrows(NullPointerException.class, () -> SingleQuotedParserToken.with(null, "\"abc\""));
    }

    @Test
    public void testWithMissingStartQuoteFails() {
        assertThrows(IllegalArgumentException.class, () -> SingleQuotedParserToken.with("abc", "abc'"));
    }

    @Test
    public void testWithMissingEndQuoteFails() {
        assertThrows(IllegalArgumentException.class, () -> SingleQuotedParserToken.with("abc", "'abc"));
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SingleQuotedParserToken token = this.createToken();

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
            protected void visit(final SingleQuotedParserToken t) {
                assertSame(token, t);
                b.append("3");
            }
        }.accept(token);
        this.checkEquals("132", b.toString());
    }

    @Override
    public SingleQuotedParserToken createToken(final String text) {
        return SingleQuotedParserToken.with(null == text ?
                        String.valueOf(null) :
                        text.substring(1, text.length() - 1),
                text);
    }

    @Override
    public String text() {
        return "'abc'";
    }

    @Override
    public SingleQuotedParserToken createDifferentToken() {
        return this.createToken("'different'");
    }

    @Override
    public Class<SingleQuotedParserToken> type() {
        return SingleQuotedParserToken.class;
    }
}
