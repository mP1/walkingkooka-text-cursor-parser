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
import walkingkooka.Cast;
import walkingkooka.text.cursor.TextCursor;

public final class UnicodeEscapeCharacterParserTest extends NonEmptyParserTestCase<UnicodeEscapeCharacterParser<ParserContext>, CharacterParserToken> {

    @Test
    public void testParseFailure() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseBacklashFails() {
        this.parseFailAndCheck("\\");
    }

    @Test
    public void testParseBacklashFails2() {
        this.parseFailAndCheck("\\-");
    }

    @Test
    public void testParseBacklashUFails() {
        this.parseFailAndCheck("\\u");
    }

    @Test
    public void testParseBacklashUFails2() {
        this.parseFailAndCheck("\\u-");
    }

    @Test
    public void testParseBacklashUOneDigitFails() {
        this.parseFailAndCheck("\\u1");
    }

    @Test
    public void testParseBacklashUOneDigitFails2() {
        this.parseFailAndCheck("\\u1-");
    }

    @Test
    public void testParseBacklashUTwoDigitFails() {
        this.parseFailAndCheck("\\u12");
    }

    @Test
    public void testParseBacklashUTwoDigitFails2() {
        this.parseFailAndCheck("\\u12-");
    }

    @Test
    public void testParseBacklashUThreeDigitFails() {
        this.parseFailAndCheck("\\u123");
    }

    @Test
    public void testParseBacklashUThreeDigitFails2() {
        this.parseFailAndCheck("\\u123-");
    }

    @Test
    public void testParseComplete() {
        this.parseAndCheck2("\\u1234", '\u1234', "\\u1234");
    }

    @Test
    public void testParseComplete2() {
        this.parseAndCheck2("\\u12345", '\u1234', "\\u1234", "5");
    }

    @Test
    public void testParseComplete3() {
        this.parseAndCheck2("\\u1234ABC", '\u1234', "\\u1234", "ABC");
    }

    @Override
    public UnicodeEscapeCharacterParser<ParserContext> createParser() {
        return UnicodeEscapeCharacterParser.get();
    }

    private TextCursor parseAndCheck2(final String in,
                                      final char value,
                                      final String text) {
        return this.parseAndCheck2(
                in,
                value,
                text,
                ""
        );
    }

    private TextCursor parseAndCheck2(final String in,
                                      final char value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck(
                in,
                CharacterParserToken.with(
                        value,
                        text
                ),
                text,
                textAfter
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), "Unicode escape char sequence");
    }

    // type.............................................................................................................

    @Override
    public Class<UnicodeEscapeCharacterParser<ParserContext>> type() {
        return Cast.to(UnicodeEscapeCharacterParser.class);
    }
}
