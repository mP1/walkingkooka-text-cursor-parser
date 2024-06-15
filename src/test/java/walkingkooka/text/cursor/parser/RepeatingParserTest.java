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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursors;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepeatingParserTest extends NonEmptyParserTestCase<RepeatingParser<ParserContext>,
        RepeatedParserToken> implements HashCodeEqualsDefinedTesting2<RepeatingParser<ParserContext>> {

    private final static String TEXT = "abc";
    private final static Parser<ParserContext> PARSER = Parsers.string(TEXT, CaseSensitivity.SENSITIVE);

    @Test
    public void testWithNullParserFails() {
        assertThrows(NullPointerException.class, () -> RepeatingParser.with(null));
    }

    @Test
    public void testWrapAnotherRepeatedParser() {
        final RepeatingParser<ParserContext> parser = this.createParser();
        assertSame(parser, Parsers.repeating(parser.cast()));
    }

    @Test
    public void testIncomplete() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testIncomplete2() {
        this.parseFailAndCheck("ab");
    }

    @Test
    public void testOnce() {
        this.parseAndCheck(TEXT,
                RepeatedParserToken.with(Lists.of(string(TEXT)), TEXT),
                TEXT,
                "");
    }

    @Test
    public void testTwiceDifferentTokens() {
        final String text1 = "'123'";
        final String text2 = "'4'";
        final String all = text1 + text2;

        this.parseAndCheck(
                RepeatingParser.with(Parsers.singleQuoted().cast()),
                this.createContext(),
                TextCursors.charSequence(all),
                RepeatedParserToken.with(Lists.of(quoted(text1), quoted(text2)), all),
                all,
                "");
    }

    @Test
    public void testMultipleTokensAndTextAfter() {
        final String text1 = "'123'";
        final String text2 = "'4'";
        final String all = text1 + text2;
        final String after = "!!!";

        this.parseAndCheck(
                RepeatingParser.with(Parsers.singleQuoted().cast()),
                this.createContext(),
                TextCursors.charSequence(all + after),
                RepeatedParserToken.with(Lists.of(quoted(text1), quoted(text2)), all),
                all,
                after);
    }

    private static SingleQuotedParserToken quoted(final String text) {
        return ParserTokens.singleQuoted(text.substring(1, text.length() - 1), text);
    }

    @Test
    public void testUntilUnmatched() {
        this.parseAndCheck(TEXT + "!!",
                RepeatedParserToken.with(Lists.of(string(TEXT)), TEXT),
                TEXT,
                "!!");
    }

    @Test
    public void testRepeating2() {
        final RepeatingParser<ParserContext> parser = this.createParser();
        assertSame(parser, parser.repeating());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), "{" + PARSER + "}");
    }

    @Override
    public RepeatingParser<ParserContext> createParser() {
        return RepeatingParser.with(PARSER);
    }

    private static StringParserToken string(final String s) {
        return ParserTokens.string(s, s);
    }

    // hashCode/equals...................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
                RepeatingParser.with(
                        Parsers.fake()
                )
        );
    }

    @Test
    public void testEqualsDifferentToString() {
        this.checkNotEquals(
                RepeatingParser.with(
                        PARSER
                ).setToString("different to string")
        );
    }

    @Override
    public RepeatingParser<ParserContext> createObject() {
        return this.createParser();
    }

    // class............................................................................................................

    @Override
    public Class<RepeatingParser<ParserContext>> type() {
        return Cast.to(RepeatingParser.class);
    }
}
