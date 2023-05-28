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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class LocalDateTimeParserTokenTest extends ValueParserTokenTestCase<LocalDateTimeParserToken, LocalDateTime> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final LocalDateTimeParserToken token = this.createToken();

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
            protected void visit(final LocalDateTimeParserToken t) {
                assertSame(token, t);
                b.append("3");
            }
        }.accept(token);
        this.checkEquals("132", b.toString());
    }

    @Override
    public LocalDateTimeParserToken createToken(final String text) {
        return LocalDateTimeParserToken.with(LocalDateTime.parse(text), text);
    }

    @Override
    public String text() {
        return "2000-12-31T12:58:59";
    }

    @Override
    public LocalDateTimeParserToken createDifferentToken() {
        return this.createToken("1999-12-31T06:30:29");
    }

    @Override
    public Class<LocalDateTimeParserToken> type() {
        return LocalDateTimeParserToken.class;
    }
}
