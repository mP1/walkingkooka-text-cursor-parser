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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class ZonedDateTimeParserTokenTest extends ValueParserTokenTestCase<ZonedDateTimeParserToken, ZonedDateTime> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final ZonedDateTimeParserToken token = this.createToken();

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
            protected void visit(final ZonedDateTimeParserToken t) {
                assertSame(token, t);
                b.append("3");
            }
        }.accept(token);
        this.checkEquals("132", b.toString());
    }

    @Override
    public ZonedDateTimeParserToken createToken(final String text) {
        return ZonedDateTimeParserToken.with(ZonedDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME), text);
    }

    @Override
    public String text() {
        return "2001-12-31T12:58:59+00:00";
    }

    @Override
    public ZonedDateTimeParserToken createDifferentToken() {
        return this.createToken("2002-01-01T01:02:59+00:00");
    }

    @Override
    public Class<ZonedDateTimeParserToken> type() {
        return ZonedDateTimeParserToken.class;
    }
}
