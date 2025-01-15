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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringParserTest extends NonEmptyParserTestCase<StringParser<ParserContext>, StringParserToken>
        implements HashCodeEqualsDefinedTesting2<StringParser<ParserContext>> {

    private final static String STRING = "abcd";
    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    // with.............................................................................................................

    @Test
    public void testWithNullStringFails() {
        assertThrows(
                NullPointerException.class,
                () -> StringParser.with(null, CASE_SENSITIVITY)
        );
    }

    @Test
    public void testWithEmptyStringFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> StringParser.with("", CASE_SENSITIVITY)
        );
    }

    @Test
    public void testWithNullCaseSensitivityFails() {
        assertThrows(
                NullPointerException.class,
                () -> StringParser.with(STRING, null)
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseIncomplete() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseIncomplete2() {
        this.parseFailAndCheck("ab");
    }

    @Test
    public void testParseIncompleteCaseInsensitive() {
        this.parseFailAndCheck(
                this.createParserCaseInsensitive(),
                "a"
        );
    }

    @Test
    public void testParseIncompleteInsensitive2() {
        this.parseFailAndCheck(
                this.createParserCaseInsensitive(),
                "ab"
        );
    }

    @Test
    public void testParseStringEoc() {
        this.parseAndCheck(STRING, this.token(), STRING, "");
    }

    @Test
    public void testParseStringEocCaseInsensitive() {
        final String text = "abCD";
        this.parseAndCheck(
                this.createParserCaseInsensitive(),
                text,
                this.token(text),
                text,
                ""
        );
    }

    @Test
    public void testParseStringIgnoresRemainder() {
        this.parseAndCheck(
                STRING + "xyz",
                this.token(),
                STRING,
                "xyz"
        );
    }

    @Test
    public void testParseStringIgnoresRemainderCaseInsensitive() {
        final String text = "abCD";
        this.parseAndCheck(
                this.createParserCaseInsensitive(),
                text + "xyz",
                this.token(text),
                text,
                "xyz"
        );
    }

    @Override
    public StringParser<ParserContext> createParser() {
        return StringParser.with(STRING, CASE_SENSITIVITY);
    }

    private StringParser<ParserContext> createParserCaseInsensitive() {
        return StringParser.with(
                STRING,
                CaseSensitivity.INSENSITIVE
        );
    }

    private StringParserToken token() {
        return this.token(STRING);
    }

    private StringParserToken token(final String text) {
        return StringParserToken.with(text, text);
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(StringParser.with("different", CASE_SENSITIVITY));
    }

    @Test
    public void testEqualsDifferentCaseSensitivity() {
        this.checkNotEquals(StringParser.with(STRING, CASE_SENSITIVITY.invert()));
    }


    @Override
    public StringParser<ParserContext> createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), CharSequences.quoteAndEscape(STRING).toString());
    }

    @Test
    public void testToStringCaseInsensitive() {
        this.toStringAndCheck(
                this.createParserCaseInsensitive(),
                CharSequences.quoteAndEscape(STRING) + " (CaseInsensitive)"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StringParser<ParserContext>> type() {
        return Cast.to(StringParser.class);
    }
}
