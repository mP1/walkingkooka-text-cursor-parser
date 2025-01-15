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

public abstract class QuotedParserTestCase<P extends QuotedParser<ParserContext>, T extends QuotedParserToken> extends NonEmptyParserTestCase<P, T> {

    QuotedParserTestCase() {
        super();
    }

    @Test
    public final void testParseFirstNotQuote() {
        this.parseFailAndCheck("abc'");
    }

    @Test
    public final void testParseMissingTerminalQuote() {
        final char quoteChar = this.quoteChar();
        this.parseThrows(quoteChar + "z", QuotedParser.missingTerminatingQuote(quoteChar));
    }

    @Test
    public final void testParseTerminatedByOther() {
        final char quoteChar = this.quoteChar();
        this.parseThrows(
                "" + quoteChar + 'z' + this.otherQuoteChar(),
                QuotedParser.missingTerminatingQuote(quoteChar)
        );
    }

    @Test
    public final void testParseInvalidBackslashEscape() {
        this.quoteParseThrows(
                "ab\\!c",
                QuotedParser.invalidBackslashEscapeChar('!')
        );
    }

    @Test
    public final void testParseInvalidUnicodeEscape() {
        this.quoteParseThrows(
                "ab\\u!c",
                QuotedParser.invalidUnicodeEscapeChar('!')
        );
    }

    @Test
    public final void testParseInvalidUnicodeEscape2() {
        this.quoteParseThrows(
                "ab\\u1!c",
                QuotedParser.invalidUnicodeEscapeChar('!')
        );
    }

    @Test
    public final void testParseInvalidUnicodeEscape3() {
        this.quoteParseThrows(
                "ab\\u12!c",
                QuotedParser.invalidUnicodeEscapeChar('!')
        );
    }

    @Test
    public final void testParseInvalidUnicodeEscape4() {
        this.quoteParseThrows(
                "ab\\u123!c",
                QuotedParser.invalidUnicodeEscapeChar('!')
        );
    }

    @Test
    public final void testParseTerminated() {
        this.quoteParseAndCheck(
                "abc",
                "abc",
                "abc"
        );
    }

    @Test
    public final void testParseTerminated2() {
        final String text = this.quote("abc");
        this.parseAndCheck(
                text + "xyz",
                this.createToken(
                        "abc",
                        text
                ),
                text,
                "xyz"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedNul() {
        this.quoteParseAndCheck(
                "abc\\0def",
                "abc\0def",
                "abc\\0def"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedTab() {
        this.quoteParseAndCheck(
                "abc\\tdef",
                "abc\tdef",
                "abc\\tdef"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedNewline() {
        this.quoteParseAndCheck(
                "abc\\ndef",
                "abc\ndef",
                "abc\\ndef"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedCarriageReturn() {
        this.quoteParseAndCheck(
                "abc\\rdef",
                "abc\rdef",
                "abc\\rdef"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedFormFeed() {
        this.quoteParseAndCheck(
                "abc\\fdef",
                "abc\fdef",
                "abc\\fdef"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedBackslash() {
        this.quoteParseAndCheck(
                "abc\\\\def",
                "abc\\def",
                "abc\\\\def"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedSingleQuote() {
        this.quoteParseAndCheck(
                "abc\\\'def",
                "abc\'def",
                "abc\\\'def"
        );
    }

    @Test
    public final void testParseIncludeBackslashEscapedDoubleQuote() {
        this.quoteParseAndCheck(
                "abc\\\"def",
                "abc\"def",
                "abc\\\"def"
        );
    }

    @Test
    public final void testParseIncludeUncodeEscaped() {
        this.quoteParseAndCheck(
                "x\\u1234y",
                "x\u1234y",
                "x\\u1234y"
        );
    }

    @Test
    public final void testParseIncludeUncodeEscaped2() {
        this.quoteParseAndCheck(
                "x\\u005Ay",
                "xZy",
                "x\\u005Ay"
        );
    }

    @Test
    public final void testParseManyEscaped() {
        this.quoteParseAndCheck(
                "x\\u005A\\0\\t\\f\\n\\r\\'\\\"y",
                "xZ\0\t\f\n\r'\"y",
                "x\\u005A\\0\\t\\f\\n\\r\\'\\\"y"
        );
    }

    private void quoteParseAndCheck(final String in,
                                    final String content,
                                    final String text) {
        this.quoteParseAndCheck(
                in,
                content,
                text,
                ""
        );
    }

    private void quoteParseAndCheck(final String in,
                                    final String content,
                                    final String text,
                                    final String textAfter) {
        final String quotedText = this.quote(text);
        this.parseAndCheck(
                this.quote(in),
                this.createToken(
                        content,
                        quotedText
                ),
                quotedText,
                textAfter
        );
    }

    private void quoteParseThrows(final String in,
                                  final String messageContains) {
        this.parseThrows(
                this.quoteChar() + in,
                messageContains
        );
    }

    final String quote(final String text) {
        final char quote = this.quoteChar();
        return quote + text + quote;
    }

    abstract char quoteChar();

    abstract char otherQuoteChar();

    abstract T createToken(final String content,
                           final String text);
}
