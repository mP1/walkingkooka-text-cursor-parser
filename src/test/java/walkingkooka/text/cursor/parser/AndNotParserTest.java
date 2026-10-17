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
import walkingkooka.text.CaseSensitivity;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AndNotParserTest extends ParserTestCase<AndNotParser<ParserContext>> {

    private final static String LEFT = "left";
    private final static String RIGHT = "right";

    private final static Parser<ParserContext> LEFT_PARSER = string(LEFT);

    private final static Parser<ParserContext> RIGHT_PARSER = string(RIGHT);

    private static Parser<ParserContext> string(final String string) {
        return Parsers.string(
            string,
            CaseSensitivity.SENSITIVE
        );
    }

    private final static ParserToken LEFT_TOKEN = ParserTokens.string(
        LEFT,
        LEFT
    );

    @Test
    public void testWithNullLeftFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createParser(
                null,
                RIGHT_PARSER
            )
        );
    }

    @Test
    public void testWithNullRightFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createParser(
                LEFT_PARSER,
                null
            )
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseLeftFailed() {
        this.parseFailAndCheck("x");
    }

    @Test
    public void testParseLeftMissingFailed() {
        this.parseFailAndCheck(
            AndNotParser.with(
                Parsers.never(),
                Parsers.fake()
            ),
            "A"
        );
    }

    @Test
    public void testParseLeftFailedRightPass() {
        this.parseFailAndCheck("right");
    }

    @Test
    public void testParseLeftMatchRightFail() {
        this.parseAndCheck(LEFT,
            LEFT_TOKEN,
            LEFT
        );
    }

    @Test
    public void testParseLeftMatchRightMissing() {
        this.parseAndCheck(
            this.createParser(
                LEFT_PARSER,
                Parsers.never()
            ),
            LEFT,
            LEFT_TOKEN,
            LEFT
        );
    }

    @Test
    public void testParseLeftMatchRightMatch() {
        this.parseFailAndCheck(
            this.createParser(
                string(LEFT),
                string(LEFT)
            ),
            LEFT
        );
    }

    @Test
    public void testParseLeftMatchRightFail2() {
        final String after = "123";
        this.parseAndCheck(
            LEFT + after,
            LEFT_TOKEN,
            LEFT,
            after
        );
    }

    @Test
    public void testSetToString() {
        this.checkEquals(
            new AndNotParser<>(
                LEFT_PARSER,
                RIGHT_PARSER,
                "Hello"
            ),
            new AndNotParser<>(
                LEFT_PARSER,
                RIGHT_PARSER,
                "Old"
            ).setToString("Hello")
        );
    }

    @Override
    public AndNotParser<ParserContext> createParser() {
        return this.createParser(
            LEFT_PARSER,
            RIGHT_PARSER
        );
    }

    private AndNotParser<ParserContext> createParser(final Parser<ParserContext> left,
                                                     final Parser<ParserContext> right) {
        return AndNotParser.with(
            left,
            right
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            this.createParser(),
            "AndNotParser\n" +
                "  and\n" +
                "    StringParser\n" +
                "      \"left\"\n" +
                "  not\n" +
                "    StringParser\n" +
                "      \"right\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<AndNotParser<ParserContext>> type() {
        return Cast.to(AndNotParser.class);
    }
}
