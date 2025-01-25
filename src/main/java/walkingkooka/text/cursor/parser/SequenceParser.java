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
    static <C extends ParserContext> SequenceParser<C> with(final List<Parser<C>> parsers) {
        Objects.requireNonNull(parsers, "parsers");

        return new SequenceParser<>(
                Lists.immutable(parsers),
                buildToString(parsers)
        );
    }

    /**
     * Concats all parsers separated by comma, and surrounding the sequence with grouping parens.
     * <pre>
     * (A, B, C)
     * </pre>
     */
    static <C extends ParserContext> String buildToString(final List<Parser<C>> parsers) {
        return parsers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(
                                ", ",
                                "(",
                                ")"
                        )
                );
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

    private final List<Parser<C>> parsers;

    // Object .............................................................................................................

    @Override
    public int hashCode() {
        return this.parsers.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SequenceParser && this.equals0((SequenceParser<?>) other);
    }

    private boolean equals0(final SequenceParser<?> other) {
        return this.parsers.equals(other.parsers);
    }

    // ParserSetToString..........................................................................................................

    @Override
    SequenceParser<C> replaceToString(final String toString) {
        return new SequenceParser<>(
                this.parsers,
                toString
        );
    }
}
