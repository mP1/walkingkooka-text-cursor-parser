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

public final class SignParserTokenTest extends ValueParserTokenTestCase<SignParserToken, Boolean> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SignParserToken token = this.createToken();

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
                b.append("3");
            }

            @Override
            protected void visit(final SignParserToken t) {
                assertSame(token, t);
                b.append("2");
            }
        }.accept(token);
        this.checkEquals("123", b.toString());
    }

    @Override
    public SignParserToken createToken(final String text) {
        return SignParserToken.with(true, text);
    }

    @Override
    public String text() {
        return "+";
    }

    @Override
    public SignParserToken createDifferentToken() {
        return SignParserToken.with(false, "-");
    }

    @Override
    public Class<SignParserToken> type() {
        return SignParserToken.class;
    }
}
