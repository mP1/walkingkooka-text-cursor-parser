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
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SurroundStringParserTest extends NonEmptyParserTestCase<SurroundStringParser<ParserContext>, StringParserToken> {

    private final static String OPEN = "<123";
    private final static String CLOSE = "456";

    @Test
    public void testWithNullOpenFails() {
        assertThrows(NullPointerException.class, () -> SurroundStringParser.with(null, CLOSE));
    }

    @Test
    public void testWithEmptyOpenFails() {
        assertThrows(IllegalArgumentException.class, () -> SurroundStringParser.with("", CLOSE));
    }

    @Test
    public void testWithNullCloseFails() {
        assertThrows(NullPointerException.class, () -> SurroundStringParser.with(OPEN, null));
    }

    @Test
    public void testWithEmptyCloseFails() {
        assertThrows(IllegalArgumentException.class, () -> SurroundStringParser.with(OPEN, ""));
    }

    @Test
    public void testOpenIncomplete() {
        this.parseFailAndCheck("<");
    }

    @Test
    public void testOpenIncomplete2() {
        this.parseFailAndCheck("<!");
    }

    @Test
    public void testOpen() {
        this.parseFailAndCheck(OPEN);
    }

    @Test
    public void testOpenWithoutClose() {
        this.parseFailAndCheck(OPEN + ".");
    }

    @Test
    public void testOpenWithoutClose2() {
        this.parseFailAndCheck(OPEN + "..");
    }

    @Test
    public void testCloseIncomplete() {
        this.parseFailAndCheck(OPEN + "..." + CLOSE.substring(0, 1));
    }

    @Test
    public void testCloseIncomplete2() {
        this.parseFailAndCheck(OPEN + "..." + CLOSE.substring(0, 2));
    }

    @Test
    public void testClose() {
        this.parseAndCheck2(OPEN + "..." + CLOSE);
    }

    @Test
    public void testClose2() {
        this.parseAndCheck2(OPEN + "..." + CLOSE.substring(0, 2) + CLOSE);
    }

    @Test
    public void testClose3() {
        this.parseAndCheck2(OPEN + "..." + CLOSE.substring(0, 2) + "." + CLOSE);
    }

    @Test
    public void testClose4() {
        this.parseAndCheck2(OPEN + "..." + OPEN + "." + CLOSE);
    }

    private void parseAndCheck2(final String text) {
        this.parseAndCheck(text, string(text), text);
    }

    @Test
    public void testClose5() {
        final String text = OPEN + "..." + OPEN + "." + CLOSE;
        final String textAfter = "!@#";
        this.parseAndCheck(text + textAfter, string(text), text, textAfter);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(),
                CharSequences.quoteAndEscape(OPEN) + "*" + CharSequences.quoteAndEscape(CLOSE));
    }

    private StringParserToken string(final String text) {
        return ParserTokens.string(text, text);
    }

    @Override
    public SurroundStringParser<ParserContext> createParser() {
        return SurroundStringParser.with(OPEN, CLOSE);
    }

    @Override
    public Class<SurroundStringParser<ParserContext>> type() {
        return Cast.to(SurroundStringParser.class);
    }
}
