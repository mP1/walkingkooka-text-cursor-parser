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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.TextCursor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Parser} that tries all parsers until one is matched and then ignores the remainder.
 */
final class AlternativesParser<C extends ParserContext> extends ParserSetToString<C> {

    /**
     * Factory that creates a {@link Parser} possibly simplifying things.
     */
    static <C extends ParserContext> Parser<C> with(final List<Parser<C>> parsers) {
        Objects.requireNonNull(parsers, "parsers");

        final List<Parser<C>> copy = Lists.array();

        // visit all parsers,. flattening any that are themselves AlternativesParser
        parsers.forEach(p -> tryFlatten(p, copy));

        final List<Parser<C>> withoutCustomToString = unwrapAllCustomToStringParsers(copy);
        final boolean allCustomToStringParsers = withoutCustomToString.size() == copy.size();

        final Parser<C> created = with0(allCustomToStringParsers ?
                withoutCustomToString :
                copy);

        return allCustomToStringParsers ?
                created.setToString(buildToString(copy)) :
                created;
    }

    /**
     * Loop over all parsers, and if any is a {@link AlternativesParser} add its children parsers, effectively
     * flattening.
     */
    private static <C extends ParserContext> void tryFlatten(final Parser<C> parser,
                                                             final List<Parser<C>> copy) {
        if (parser instanceof AlternativesParser) {
            final AlternativesParser<C> alt = parser.cast();
            copy.addAll(alt.parsers);
        } else {
            copy.add(parser);
        }
    }

    /**
     * Loop over all parsers, unwrapping all {@link CustomToStringParser}.
     */
    private static <C extends ParserContext> List<Parser<C>> unwrapAllCustomToStringParsers(final List<Parser<C>> parsers) {
        return parsers.stream()
                .filter(p -> p instanceof CustomToStringParser)
                .map(p -> ((CustomToStringParser<C>)p).parser.cast())
                .map(Cast::<Parser<C>>to)
                .collect(Collectors.toList());
    }

    /**
     * The actual factory method that accepts the copy of parsers that have been processed and possibly simplified.
     */
    private static <C extends ParserContext> Parser<C> with0(final List<Parser<C>> parsers) {
        Parser<C> parser;

        switch (parsers.size()) {
            case 0:
                throw new IllegalArgumentException("At least one parser must be provided");
            case 1:
                parser = parsers.get(0).cast();
                break;
            default:
                parser = new AlternativesParser<>(
                        parsers,
                        buildToString(parsers)
                );
                break;
        }

        return parser;
    }

    /**
     * Private ctor
     */
    private AlternativesParser(final List<Parser<C>> parsers,
                               final String toString) {
        super(toString);
        this.parsers = parsers;
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
                toString
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.parsers.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                (other instanceof AlternativesParser && this.equals0((AlternativesParser<?>) other));
    }

    private boolean equals0(final AlternativesParser<?> other) {
        return this.parsers.equals(other.parsers);
    }

    private static <CC extends ParserContext> String buildToString(final List<Parser<CC>> parsers) {
        return parsers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | ", "(", ")"));
    }
}
