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

import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link Parser} that consumes characters or text from a {@link TextCursor} and returns a {@link ParserToken token}.
 */
public interface Parser<C extends ParserContext> {

    /**
     * Attempts to parse the text given by the {@link TextCursor}, only advancing if the required token was matched.
     */
    Optional<ParserToken> parse(final TextCursor cursor, final C context);

    /**
     * Helper that parses the given text, failing if the text was not consumed completely.
     */
    default ParserToken parseText(final String text,
                                  final C context) {
        try {
            return this.orFailIfCursorNotEmpty(
                    ParserReporters.basic()
            ).parse(
                    TextCursors.charSequence(text),
                    context
            ).orElseThrow(() -> new InvalidCharacterException(text, 0));
        } catch (final InvalidCharacterException cause) {
            throw cause.clearColumnAndLine();
//        } catch (final ParserReporterException cause) {
//            final Throwable cause2 = cause.getCause();
//            if (cause2 instanceof RuntimeException) {
//                throw (RuntimeException) cause2;
//            }
//
//            throw cause.lineInfo()
//                    .emptyTextOrInvalidCharacterExceptionOrLast("text");
        } catch (final ParserException cause) {
            final Throwable wrapped = cause.getCause();
            if (wrapped instanceof RuntimeException) {
                throw (RuntimeException) wrapped;
            }
            throw new IllegalArgumentException(wrapped);
        }
    }

    int OPTIONAL_MIN_COUNT = 0;

    /**
     * Tests if this parser is optional, aka the {@link #minCount()} == 0
     */
    default boolean isOptional() {
        return OPTIONAL_MIN_COUNT == this.minCount();
    }

    int OPTIONAL_MAX_COUNT = 1;

    /**
     * Tests if this parser is required, when {@link #minCount()} GTE 1
     */
    default boolean isRequired() {
        return this.minCount() >= 1;
    }

    /**
     * Returns the min count for this parser.
     * For optional parsers this will return 0, required will return 1, and repeating any number.
     */
    int minCount();

    /**
     * Returns the max count for this parser.
     * For optional and required parsers this will return 1, repeating any number.
     */
    int maxCount();

    // parser building..................................................................................................

    default Parser<C> and(final Parser<C> next) {
        Objects.requireNonNull(next, "next");

        return Parsers.sequence(
                Lists.of(
                        this,
                        next
                )
        );
    }

    /**
     * Adds a post condition, namely this {@link Parser} when it returns a {@link ParserToken} must also be empty,
     * otherwise the {@link TextCursor} is restored.
     */
    default Parser<C> andEmptyTextCursor() {
        return Parsers.andEmptyTextCursor(this);
    }

    /**
     * Creates a parser that matches this parser and fails the given parser.
     */
    default Parser<C> andNot(final Parser<C> parser) {
        return Parsers.andNot(this, parser);
    }

    /**
     * Returns a {@link Parser} that is optional.
     */
    default Parser<C> optional() {
        return this.repeat(
                OPTIONAL_MIN_COUNT,
                1
        );
    }

    /**
     * Returns a {@link Parser} that is required.
     */
    default Parser<C> required() {
        return this.repeat(
                1,
                1
        );
    }

    /**
     * Returns a {@link Parser} that matches this OR the given {@link Parser} tokens.
     */
    default Parser<C> or(final Parser<C> parser) {
        Objects.requireNonNull(parser, "parser");

        return Parsers.alternatives(
                Lists.of(
                        this.cast(),
                        parser.cast()
                )
        );
    }

    /**
     * {@see RepeatingParser}
     */
    default Parser<C> repeat(final int minCount,
                             final int maxCount) {
        return Parsers.repeating(
                minCount,
                maxCount,
                this
        );
    }

    int DEFAULT_REPEAT_MIN_COUNT = 0;

    int DEFAULT_REPEAT_MAX_COUNT = Integer.MAX_VALUE;

    /**
     * Returns a {@link Parser} that matches zero or more repetitions of the given tokens.
     */
    default Parser<C> repeating() {
        return this.repeat(
                DEFAULT_REPEAT_MIN_COUNT,
                DEFAULT_REPEAT_MAX_COUNT
        );
    }

    /**
     * Returns a {@link Parser} that returns the given {@link String toString}.
     */
    default Parser<C> setToString(final String toString) {
        return Parsers.customToString(
                this,
                toString
        );
    }

    /**
     * {@see TransformingParser}
     */
    default Parser<C> transform(final BiFunction<ParserToken, C, ParserToken> transformer) {
        return TransformingParser.with(
                this,
                transformer
        );
    }

    /**
     * The {@link ParserReporter} will be triggered if this {@link Parser} failed and returned a {@link Optional#empty()}.
     */
    default Parser<C> orReport(final ParserReporter<C> reporter) {
        return this.or(
                Parsers.report(
                        ParserReporterCondition.ALWAYS,
                        reporter,
                        this.cast()
                )
        );
    }

    /**
     * Returns a {@link Parser} which will use the {@link ParserReporter} if the {@link TextCursor} is not empty.
     */
    default Parser<C> orFailIfCursorNotEmpty(final ParserReporter<C> reporter) {
        return Parsers.report(
                ParserReporterCondition.NOT_EMPTY,
                reporter,
                this
        );
    }

    /**
     * Helper that makes casting and working around generics a little less noisy.
     */
    default <P extends Parser<?>> P cast() {
        return Cast.to(this);
    }
}
