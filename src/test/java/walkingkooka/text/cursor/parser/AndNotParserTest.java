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

    @Test
    public void testWithNullLeftFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createParser(
                        null,
                        this.right()
                )
        );
    }

    @Test
    public void testWithNullRightFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createParser(
                        this.left(),
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
                this.leftToken(),
                LEFT);
    }

    private StringParserToken leftToken() {
        return ParserTokens.string(LEFT, LEFT);
    }

    @Test
    public void testParseLeftMatchRightMissing() {
        this.parseAndCheck(
                this.createParser(
                        this.left(),
                        Parsers.never()
                ),
                LEFT,
                this.leftToken(),
                LEFT
        );
    }

    @Test
    public void testParseLeftMatchRightMatch() {
        this.parseFailAndCheck(this.createParser(string(LEFT), string(LEFT)),
                LEFT);
    }

    @Test
    public void testParseLeftMatchRightFail2() {
        final String after = "123";
        this.parseAndCheck(LEFT + after,
                this.leftToken(),
                LEFT,
                after);
    }

    @Test
    public void testSetToString() {
        this.checkEquals(
                new AndNotParser<>(
                        left(),
                        right(),
                        "Hello"
                ),
                new AndNotParser<>(
                        left(),
                        right(),
                        "Old"
                ).setToString("Hello")
        );
    }

    @Override
    public AndNotParser<ParserContext> createParser() {
        return this.createParser(this.left(), this.right());
    }

    private AndNotParser<ParserContext> createParser(final Parser<ParserContext> left,
                                                     final Parser<ParserContext> right) {
        return AndNotParser.with(left, right);
    }

    private Parser<ParserContext> left() {
        return string(LEFT);
    }

    private Parser<ParserContext> right() {
        return string(RIGHT);
    }

    private Parser<ParserContext> string(final String string) {
        return Parsers.string(string, CaseSensitivity.SENSITIVE);
    }

    // class............................................................................................................

    @Override
    public void testAllConstructorsVisibility() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<AndNotParser<ParserContext>> type() {
        return Cast.to(AndNotParser.class);
    }
}
