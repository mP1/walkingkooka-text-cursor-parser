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
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin that includes numerous helpers to assist parsing and verifying the outcome for success and failures.
 */
public interface ParserTesting2<P extends Parser<C>,
        C extends ParserContext>
        extends ParserTesting {

    /**
     * Provides or creates the {@link Parser} being tested.
     */
    P createParser();

    /**
     * Provides the required {@link ParserContext} used by the {@link Parser}
     */
    C createContext();

    // parseAndCheck....................................................................................................

    default TextCursor parseAndCheck(final String cursorText,
                                     final ParserToken token,
                                     final String text) {
        return this.parseAndCheck(
                cursorText,
                token,
                text,
                ""
        );
    }

    default TextCursor parseAndCheck(final String cursorText,
                                     final ParserToken token,
                                     final String text,
                                     final String textAfter) {
        return this.parseAndCheck(
                TextCursors.charSequence(cursorText),
                token,
                text,
                textAfter
        );
    }

    default TextCursor parseAndCheck(final TextCursor cursor,
                                     final ParserToken token,
                                     final String text) {
        return this.parseAndCheck(
                cursor,
                token,
                text,
                ""
        );
    }

    default TextCursor parseAndCheck(final TextCursor cursor,
                                     final ParserToken token,
                                     final String text,
                                     final String textAfter) {
        return this.parseAndCheck(
                this.createParser(),
                this.createContext(),
                cursor,
                token,
                text,
                textAfter
        );
    }

    default TextCursor parseAndCheck(final Parser<C> parser,
                                     final String cursorText,
                                     final ParserToken token,
                                     final String text) {
        return this.parseAndCheck(
                parser,
                cursorText,
                token,
                text,
                ""
        );
    }

    default TextCursor parseAndCheck(final Parser<C> parser,
                                     final String cursorText,
                                     final ParserToken token,
                                     final String text,
                                     final String textAfter) {
        return this.parseAndCheck(
                parser,
                this.createContext(),
                cursorText,
                token,
                text,
                textAfter
        );
    }

    // parseFailAndCheck................................................................................................

    default TextCursor parseFailAndCheck(final String cursorText) {
        return this.parseFailAndCheck(
                TextCursors.charSequence(cursorText)
        );
    }

    default TextCursor parseFailAndCheck(final TextCursor cursor) {
        return this.parseFailAndCheck(
                this.createParser(),
                this.createContext(),
                cursor
        );
    }

    default TextCursor parseFailAndCheck(final Parser<C> parser,
                                         final String cursorText) {
        return this.parseFailAndCheck(
                parser,
                this.createContext(),
                cursorText
        );
    }

    // parseThrows......................................................................................................

    default void parseThrowsInvalidCharacterException(final String cursorText,
                                                      final char c,
                                                      final String column,
                                                      final int row) {
        this.parseThrowsInvalidCharacterException(
                cursorText,
                c,
                column.length(),
                row
        );
    }

    default void parseThrowsInvalidCharacterException(final String text,
                                                      final char c,
                                                      final int column,
                                                      final int row) {
        this.parseThrowsInvalidCharacterException(
                this.createParser(),
                this.createContext(),
                text,
                c,
                column,
                row
        );
    }

    default void parseThrowsInvalidCharacterException(final P parser,
                                                      final C context,
                                                      final String text,
                                                      final char c,
                                                      final int column,
                                                      final int row) {
        this.parseThrowsInvalidCharacterException(
                parser,
                context,
                TextCursors.charSequence(text),
                c,
                column,
                row
        );
    }

    default void parseThrowsInvalidCharacterException(final P parser,
                                                      final C context,
                                                      final TextCursor text,
                                                      final char c,
                                                      final int column,
                                                      final int row) {
        // Message format from BasicParserReporter
        final ParserException thrown = assertThrows(
                ParserException.class,
                () -> this.parse(
                        parser,
                        text,
                        context
                )
        );

        final String message = "Invalid character " + CharSequences.quoteAndEscape(c) + " at (" + column + "," + row + ")";
        final String thrownMessage = thrown.getMessage();

        this.checkEquals(
                true,
                thrownMessage.startsWith(message),
                () -> "parse " + text
        );
    }

    default void parseThrowsEndOfText(final String cursorText) {
        this.parseThrowsEndOfText(
                cursorText,
                cursorText.length() + 1,
                1
        );
    }

    default void parseThrowsEndOfText(final String cursorText,
                                      final int column,
                                      final int row) {
        // Message format from BasicParserReporter
        this.parseThrowsEndOfText(
                this.createParser(),
                cursorText,
                column,
                row
        );
    }

    default void parseThrowsEndOfText(final Parser<C> parser,
                                      final String cursorText,
                                      final int column,
                                      final int row) {
        this.parseThrowsEndOfText(
                parser,
                this.createContext(),
                cursorText,
                column,
                row
        );
    }

    default void parseThrows(final String cursorText,
                             final String expected) {
        this.parseThrows(
                TextCursors.charSequence(cursorText),
                expected
        );
    }

    default void parseThrows(final TextCursor cursor,
                             final String expected) {
        this.parseThrows(
                this.createParser(),
                this.createContext(),
                cursor,
                expected
        );
    }

    default void parseThrows(final Parser<C> parser,
                             final String cursor,
                             final String expected) {
        this.parseThrows(
                parser,
                TextCursors.charSequence(cursor),
                expected
        );
    }

    default void parseThrows(final Parser<C> parser,
                             final TextCursor cursor,
                             final String expected) {
        this.parseThrows(
                parser,
                this.createContext(),
                cursor,
                expected
        );
    }

    // isOptional.......................................................................................................

    @Test
    default void testIsOptional() {
        final P parser = this.createParser();

        this.isOptionalAndCheck(
                parser,
                parser.minCount() == 0
        );
    }

    default void isOptionalAndCheck(final boolean expected) {
        this.isOptionalAndCheck(
                this.createParser(),
                expected
        );
    }

    // isRequired.......................................................................................................

    @Test
    default void testIsRequired() {
        final P parser = this.createParser();

        this.isRequiredAndCheck(
                parser,
                parser.minCount() >= 1
        );
    }

    default void isRequiredAndCheck(final boolean expected) {
        this.isRequiredAndCheck(
                this.createParser(),
                expected
        );
    }

    // minCount.........................................................................................................

    @Test
    default void testMinCountGreaterThanEqualZero() {
        final P parser = this.createParser();
        final int minCount = parser.minCount();

        this.checkNotEquals(
                -1,
                Math.signum(minCount),
                () -> parser + " minCount=" + minCount
        );
    }

    default void minCountAndCheck(final int expected) {
        this.minCountAndCheck(
                this.createParser(),
                expected
        );
    }

    // maxCount.........................................................................................................

    @Test
    default void testMaxCountGreaterThanEqualMinCount() {
        final P parser = this.createParser();
        final int minCount = parser.minCount();
        final int maxCount = parser.maxCount();

        this.checkNotEquals(
                -1,
                Math.signum(maxCount - minCount),
                () -> parser + " maxCount " + maxCount + " < minCount=" + minCount
        );
    }

    default void maxCountAndCheck(final int expected) {
        this.maxCountAndCheck(
                this.createParser(),
                expected
        );
    }
}
