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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Parser} that requires all parsers are matched in order returning all tokens within a {@link SequenceParserToken}
 */
final class SequenceParser<C extends ParserContext> extends NonEmptyParser<C>
        implements RequiredParser<C> {

    /**
     * Factory method only called by {@link SequenceParserBuilder#build()}
     */
    static <C extends ParserContext> Parser<C> with(final List<Parser<C>> parsers) {
        Objects.requireNonNull(parsers, "parsers");

        final List<Parser<C>> flat = Lists.array();

        // visit all parsers, flattening any that are themselves AlternativesParser
        parsers.forEach(
                p -> tryFlatten(p, flat)
        );

        final Parser<C> result;

        switch (flat.size()) {
            case 0:
                throw new IllegalArgumentException("Empty parsers");
            case 1:
                result = flat.get(0);
                break;
            default:
                result = new SequenceParser<>(
                        flat,
                        buildToString(flat)
                );
        }

        return result;
    }

    /**
     * Loop over all parsers, and if any is a {@link AlternativesParser} add its children parsers, effectively
     * flattening.
     */
    private static <C extends ParserContext> void tryFlatten(final Parser<C> parser,
                                                             final List<Parser<C>> flat) {
        if (parser instanceof SequenceParser) {
            final SequenceParser<C> sequence = parser.cast();
            for (final Parser<C> p : sequence.parsers) {
                tryFlatten(
                        p,
                        flat
                );
            }
        } else {
            flat.add(parser);
        }
    }

    /**
     * Concats all parsers separated by comma, any {@link AlternativesParser} will have extra grouping parens around them.
     * <pre>
     * (A, B, C)
     * </pre>
     */
    static <C extends ParserContext> String buildToString(final List<Parser<C>> parsers) {
        return parsers.stream()
                .map(SequenceParser::parserToString)
                .collect(Collectors.joining(
                        ", "
                        )
                );
    }

    /**
     * Only add grouping parens if any {@link AlternativesParser} has a NON custom {@link Object#toString()}.
     */
    private static <C extends ParserContext> String parserToString(final Parser<C> parser) {
        String toString = parser.toString();

        if (parser instanceof AlternativesParser) {
            final AlternativesParser<?> alternativesParser = parser.cast();
            if (false == alternativesParser.customToString) {
                toString = "(" + toString + ")";
            }
        }

        return toString;
    }

    private SequenceParser(final List<Parser<C>> parsers,
                           final String toString) {
        super(toString);
        this.parsers = parsers;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint start) {
        Optional<ParserToken> result = Optional.empty();

        final List<ParserToken> tokens = Lists.array();

        for (final Parser<C> parser : this.parsers) {
            final Optional<ParserToken> token = parser.parse(cursor, context);
            if (token.isPresent()) {
                tokens.add(token.get());
                continue;
            }
            if (parser.isRequired()) {
                tokens.clear();
                break;
            }
        }

        if (false == tokens.isEmpty()) {
            result = Optional.of(
                    SequenceParserToken.with(
                            tokens,
                            start.textBetween()
                                    .toString()
                    )
            );
        }

        // caller will restore cursor when $result is empty
        return result;
    }

    // @VisibleForTesting
    final List<Parser<C>> parsers;

    // ParserSetToString..........................................................................................................

    @Override
    SequenceParser<C> replaceToString(final String toString) {
        return new SequenceParser<>(
                this.parsers,
                toString
        );
    }

    boolean customToString;

    // Object ..........................................................................................................

    @Override //
    int hashCode0() {
        return this.parsers.hashCode();
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final SequenceParser<?> otherSequenceParser = (SequenceParser<?>) other;

        return this.parsers.equals(otherSequenceParser.parsers);
    }
}
