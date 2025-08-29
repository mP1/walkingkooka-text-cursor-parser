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

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class LocalTimeParserTokenTest extends ValueParserTokenTestCase<LocalTimeParserToken, LocalTime> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final LocalTimeParserToken token = this.createToken();

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
            protected void visit(final LocalTimeParserToken t) {
                assertSame(token, t);
                b.append("3");
            }
        }.accept(token);
        this.checkEquals("132", b.toString());
    }

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createToken(),
            "LocalTime \"12:58:59\" 12:58:59 (java.time.LocalTime)\n"
        );
    }

    @Override
    public LocalTimeParserToken createToken(final String text) {
        return LocalTimeParserToken.with(LocalTime.parse(text), text);
    }

    @Override
    public String text() {
        return "12:58:59";
    }

    @Override
    public LocalTimeParserToken createDifferentToken() {
        return this.createToken("12:01:02");
    }

    @Override
    public Class<LocalTimeParserToken> type() {
        return LocalTimeParserToken.class;
    }
}
