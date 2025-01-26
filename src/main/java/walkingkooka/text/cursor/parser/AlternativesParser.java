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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Parser} that tries all parsers until one is matched and then ignores the remaining parsers.
 */
final class AlternativesParser<C extends ParserContext> extends ParserSetToString<C> implements RequiredParser<C> {

    /**
     * Factory that creates a {@link Parser} possibly simplifying things flattening any given {@link AlternativesParser}
     * expanding their parsers into a horizontal line.
     * <pre>
     * A | B
     * C | D
     * ->
     * A | B | C | D
     * </pre>
     */
    static <C extends ParserContext> Parser<C> with(final List<Parser<C>> parsers) {
        Objects.requireNonNull(parsers, "parsers");

        final List<Parser<C>> unique = Lists.array();

        // visit all parsers, flattening any that are themselves AlternativesParser
        parsers.forEach(
                p -> tryFlatten(p, unique)
        );

        final Parser<C> result;

        switch (unique.size()) {
            case 0:
                throw new IllegalArgumentException("Empty parsers");
            case 1:
                result = unique.get(0);
                break;
            default:
                result = new AlternativesParser<>(
                        unique,
                        buildToString(unique),
                        false // customToString=false
                );
        }

        return result;
    }

    /**
     * Loop over all parsers, and if any is a {@link AlternativesParser} add its children parsers, effectively
     * flattening.
     */
    private static <C extends ParserContext> void tryFlatten(final Parser<C> parser,
                                                             final List<Parser<C>> unique) {
        if (parser instanceof AlternativesParser) {
            final AlternativesParser<C> alt = parser.cast();
            for (final Parser<C> p : alt.parsers) {
                tryFlatten(
                        p,
                        unique
                );
            }
        } else {
            if (false == unique.contains(parser)) {
                unique.add(parser);
            }
        }
    }

    private static <CC extends ParserContext> String buildToString(final List<Parser<CC>> parsers) {
        return parsers.stream()
                .map(AlternativesParser::parserToString)
                .collect(
                        Collectors.joining(" | ")
                );
    }

    /**
     * Only add grouping parens if any {@link SequenceParser} has a NON custom {@link Object#toString()}.
     */
    private static <C extends ParserContext> String parserToString(final Parser<C> parser) {
        String toString = parser.toString();

        if (parser instanceof SequenceParser) {
            final SequenceParser<?> sequenceParser = parser.cast();
            if (false == sequenceParser.customToString) {
                toString = "(" + toString + ")";
            }
        }

        return toString;
    }

    /**
     * Private ctor
     */
    private AlternativesParser(final List<Parser<C>> parsers,
                               final String toString,
                               final boolean customToString) {
        super(toString);
        this.parsers = parsers;
        this.customToString = customToString;
    }

    /**
     * Try all parsers even when the {@link TextCursor} is empty. This is necessary,
     * because one parser might be a {@link ReportingParser} which wants to report a parsing failure.
     */
    @Override
    public Optional<ParserToken> parse(final TextCursor cursor, final C context) {
        Optional<ParserToken> token = Optional.empty();

        for (Parser<C> parser : this.parsers) {
            Optional<ParserToken> possible = parser.parse(cursor, context);
            if (possible.isPresent()) {
                token = possible;
                break;
            }
        }

        return token;
    }

    @Override
    public Parser<C> or(final Parser<C> parser) {
        Objects.requireNonNull(parser, "parser");

        // append the new parser to the current list and make a new AlternativesParser
        final List<Parser<C>> parsers = Lists.array();
        parsers.addAll(this.parsers);
        parsers.add(parser.cast());

        return AlternativesParser.with(parsers);
    }

    // @VisibleForTesting
    final List<Parser<C>> parsers;

    // ParserSetToString..........................................................................................................

    @Override
    AlternativesParser<C> replaceToString(final String toString) {
        return new AlternativesParser<>(
                this.parsers,
                toString,
                true // customToString=true
        );
    }

    // Object...........................................................................................................

    @Override //
    int hashCode0() {
        return Objects.hash(this.parsers);
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final AlternativesParser<?> otherAlternativeParser = other.cast();

        return this.parsers.equals(
                otherAlternativeParser.parsers
        );
    }

    boolean customToString;
}
