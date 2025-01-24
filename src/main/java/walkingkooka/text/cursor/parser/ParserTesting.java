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

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.TreePrintableTesting;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin that includes numerous helpers to assist parsing and verifying the outcome for success and failures.
 */
public interface ParserTesting extends TreePrintableTesting {

    // parseAndCheck....................................................................................................

    default <CC extends ParserContext> TextCursor parseAndCheck(final Parser<CC> parser,
                                                                final CC context,
                                                                final String cursorText,
                                                                final ParserToken token,
                                                                final String text) {
        return this.parseAndCheck(parser, context, cursorText, token, text, "");
    }

    default <CC extends ParserContext> TextCursor parseAndCheck(final Parser<CC> parser,
                                                                final CC context,
                                                                final String cursorText,
                                                                final ParserToken token,
                                                                final String text,
                                                                final String textAfter) {
        return this.parseAndCheck(parser, context, TextCursors.charSequence(cursorText), token, text, textAfter);
    }

    default <CC extends ParserContext> TextCursor parseAndCheck(final Parser<CC> parser,
                                                                final CC context,
                                                                final TextCursor cursor,
                                                                final ParserToken token,
                                                                final String text) {
        return this.parseAndCheck(parser, context, cursor, Optional.of(token), text, "");
    }

    default <CC extends ParserContext> TextCursor parseAndCheck(final Parser<CC> parser,
                                                                final CC context,
                                                                final TextCursor cursor,
                                                                final ParserToken token,
                                                                final String text,
                                                                final String textAfter) {
        return this.parseAndCheck(parser, context, cursor, Optional.of(token), text, textAfter);
    }

    default <CC extends ParserContext> TextCursor parseAndCheck(final Parser<CC> parser,
                                                                final CC context,
                                                                final TextCursor cursor,
                                                                final Optional<ParserToken> token,
                                                                final String text,
                                                                final String textAfter) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(text, "text");

        final TextCursorSavePoint before = cursor.save();
        final Optional<ParserToken> result = this.parse(parser, cursor, context);

        final CharSequence consumed = before.textBetween();

        final TextCursorSavePoint after = cursor.save();
        cursor.end();

        final String textRemaining = after.textBetween().toString();
        this.checkEquals(
                token,
                result,
                () -> "text:\n" + CharSequences.quoteAndEscape(consumed.length() == 0 ?
                        after.textBetween() :
                        consumed) + "\nunconsumed text:\n" + textRemaining
        );

        this.checkEquals(text, consumed, "incorrect consumed text");
        this.checkEquals(text, result.map(ParserToken::text).orElse(""), "token consume text is incorrect");
        this.checkEquals(textAfter, textRemaining, "Incorrect text after match");

        after.restore();
        return cursor;
    }

    // parseFailAndCheck................................................................................................

    default <CC extends ParserContext> TextCursor parseFailAndCheck(final Parser<CC> parser,
                                                                    final CC context,
                                                                    final String cursorText) {
        return this.parseFailAndCheck(parser, context, TextCursors.charSequence(cursorText));
    }

    default <CC extends ParserContext> TextCursor parseFailAndCheck(final Parser<CC> parser,
                                                                    final CC context,
                                                                    final TextCursor cursor) {
        final TextCursorSavePoint before = cursor.save();
        final Optional<ParserToken> result = this.parse(parser, cursor, context);
        if (result.isPresent()) {
            final TextCursorSavePoint after = cursor.save();

            this.checkEquals(
                    null,
                    result.orElse(null),
                    () -> "Expected no token from parsing text consumed:\n" + before.textBetween() + "\ntext left: " + after.textBetween()
            );
        }
        return cursor;
    }

    // asserting ParserToken with pretty dump support...................................................................

    default void checkEquals(final Optional<? extends ParserToken> expected,
                             final Optional<? extends ParserToken> actual,
                             final Supplier<String> message) {
        this.checkEquals(
                expected.orElse(null),
                actual.orElse(null),
                message
        );
    }

    default void checkEquals(final List<ParserToken> expected,
                             final List<ParserToken> actual,
                             final Supplier<String> message) {
        final Function<ParserToken, String> mapper = (t) -> t.treeToString(INDENTATION, EOL);
        this.checkEquals(
                expected.stream().map(mapper).collect(Collectors.joining()),
                actual.stream().map(mapper).collect(Collectors.joining()),
                message
        );
    }

    // parseThrowsEndOfText.............................................................................................

    default <CC extends ParserContext> void parseThrowsEndOfText(final Parser<CC> parser,
                                                                 final CC context,
                                                                 final String text,
                                                                 final int column,
                                                                 final int row) {
        final ParserException thrown = assertThrows(
                ParserException.class,
                () -> this.parse(
                        parser,
                        TextCursors.charSequence(text),
                        context
                )
        );

        final String message = endOfText(column, row);
        final String thrownMessage = thrown.getMessage();

        this.checkEquals(
                true,
                thrownMessage.startsWith(message),
                () -> "parse " + text
        );
    }

    default <CC extends ParserContext> void parseThrows(final Parser<CC> parser,
                                                        final CC context,
                                                        final TextCursor cursor,
                                                        final String expected) {
        final TextCursorSavePoint save = cursor.save();
        cursor.end();

        final String text = save.textBetween()
                .toString();
        save.restore();

        final ParserException thrown = assertThrows(
                ParserException.class,
                () -> this.parse(
                        parser,
                        cursor,
                        context
                )
        );

        this.checkEquals(
                expected,
                thrown.getMessage(),
                () -> "parse " + text
        );
    }

    // parse............................................................................................................

    default <CC extends ParserContext> Optional<ParserToken> parse(final Parser<CC> parser,
                                                                   final TextCursor cursor,
                                                                   final CC context) {
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(cursor, "cursor");

        final Optional<ParserToken> result = parser.parse(cursor, context);
        this.checkNotEquals(
                null,
                result,
                () -> "parser " + parser + " returned null result"
        );
        return result;
    }

    default DateTimeContext dateTimeContext() {
        return DateTimeContexts.locale(
                Locale.ENGLISH,
                1900,
                20,
                LocalDateTime::now
        );
    }

    default DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(MathContext.DECIMAL32);
    }

    default String endOfText(final int column, final int row) {
        return "End of text at (" + column + "," + row + ")";
    }

    // minCount.........................................................................................................

    default void minCountAndCheck(final Parser<?> parser,
                                  final int expected) {
        this.checkEquals(
                expected,
                parser.minCount(),
                parser::toString
        );
    }

    // maxCount.........................................................................................................

    default void maxCountAndCheck(final Parser<?> parser,
                                  final int expected) {
        this.checkEquals(
                expected,
                parser.maxCount(),
                parser::toString
        );
    }
}
