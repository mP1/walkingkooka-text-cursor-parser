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

/**
 * A {@link Parser} that only matches one or more tokens matched by a different provided {@link Parser}.
 */
final class RepeatingParser<C extends ParserContext> extends NonEmptyParser<C> {

    static <C extends ParserContext> Parser<C> with(final int minCount,
                                                    final int maxCount,
                                                    final Parser<C> parser) {
        if (minCount < 0) {
            throw new IllegalArgumentException("Invalid min count " + minCount + " < 0");
        }
        if (maxCount < minCount) {
            throw new IllegalArgumentException("Invalid max count " + maxCount + " < min count " + minCount);
        }
        if (0 == minCount && 0 == maxCount) {
            throw new IllegalArgumentException("Invalid min count " + minCount + " and max count " + maxCount);
        }
        Objects.requireNonNull(parser, "parser");

        Parser<C> result;

        int newRepeatingMinCount = minCount;
        int newRepeatingMaxCount = maxCount;

        if (parser instanceof RepeatingParser) {
            final RepeatingParser<C> repeatingParser = (RepeatingParser<C>) parser;

            final int repeatingMinCount = repeatingParser.minCount();
            final int repeatingMaxCount = repeatingParser.maxCount();
            Parser<C> wrapped = repeatingParser.parser;

            newRepeatingMinCount = saturatedMultiply(
                    minCount,
                    repeatingMinCount
            );
            newRepeatingMaxCount = saturatedMultiply(
                    maxCount,
                    repeatingMaxCount
            );

            // not repeating just return the original wrapped Parser
            if (1 == newRepeatingMinCount && 1 == newRepeatingMaxCount) {
                result = wrapped;
            } else {
                // new RepeatingParser is different so create
                if (repeatingMinCount != newRepeatingMinCount || repeatingMaxCount != newRepeatingMaxCount) {
                    result = with0(
                            newRepeatingMinCount,
                            newRepeatingMaxCount,
                            wrapped
                    );
                } else {
                    // no change in min/max return the original parser
                    result = parser;
                }
            }
        } else {
            // no need to wrap parser
            if (parser.minCount() == newRepeatingMinCount && parser.maxCount() == newRepeatingMaxCount) {
                result = parser;
            } else {
                // wrap parser and repeat it
                result = with0(
                        newRepeatingMinCount,
                        newRepeatingMaxCount,
                        parser
                );
            }
        }

        return result;
    }

    private static int saturatedMultiply(final int a,
                                         final int b) {
        int c;

        if (DEFAULT_REPEAT_MAX_COUNT == a && DEFAULT_REPEAT_MAX_COUNT == b) {
            c = DEFAULT_REPEAT_MAX_COUNT;
        } else {
            final long d = a * b;
            c = d < Integer.MIN_VALUE ?
                    Integer.MIN_VALUE :
                    d > Integer.MAX_VALUE ?
                            Integer.MAX_VALUE :
                            (int) d;
        }

        return c;
    }

    private static <C extends ParserContext> Parser<C> with0(final int minCount,
                                                             final int maxCount,
                                                             final Parser<C> parser) {
        return new RepeatingParser<>(
                minCount,
                maxCount,
                parser,
                buildRepeatingToString(
                        minCount,
                        maxCount,
                        parser
                )
        );
    }

    // [PARSER]
    // {PARSER}{1,*}
    // {PARSER}{2,3}
    private static String buildRepeatingToString(final int minCount,
                                                 final int maxCount,
                                                 final Parser<?> parser) {
        final StringBuilder builder = new StringBuilder();

        if (0 == minCount && 1 == maxCount) {
            builder.append('[');
            builder.append(parser);
            builder.append(']');
        } else {
            builder.append('{');
            builder.append(parser);
            builder.append('}');

            if (DEFAULT_REPEAT_MIN_COUNT != minCount || DEFAULT_REPEAT_MAX_COUNT != maxCount) {
                builder.append('{');

                boolean separator = false;

                if (0 != minCount) {
                    builder.append(minCount);
                    separator = true;
                }

                if (minCount != maxCount) {
                    if (separator) {
                        builder.append(',');
                    }
                    builder.append(
                            DEFAULT_REPEAT_MAX_COUNT == maxCount ?
                                    "*" :
                                    String.valueOf(maxCount)
                    );
                }

                builder.append('}');
            }
        }

        return builder.toString();
    }

    private RepeatingParser(final int minCount,
                            final int maxCount,
                            final Parser<C> parser,
                            final String toString) {
        super(toString);

        this.minCount = minCount;
        this.maxCount = maxCount;
        this.parser = parser;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint start) {
        final Parser<C> parser = this.parser;
        final int maxCount = this.maxCount;

        List<ParserToken> tokens = null; // lazily create

        for (; ; ) {
            final Optional<ParserToken> maybe = parser.parse(
                    cursor,
                    context
            );
            if (false == maybe.isPresent()) {
                break;
            }
            if (null == tokens) {
                tokens = Lists.array();
            }
            tokens.add(maybe.get());
            if (tokens.size() == maxCount) {
                break;
            }
        }

        return Optional.ofNullable(
                null == tokens || tokens.size() < this.minCount ?
                        null :
                        // if this parser is optional min=0 & max=1 DONT wrap the only matched token in a RepeatingParserToken
                        this.isOptional() && 1 == maxCount ?
                                optionalResult(tokens) :
                                nonOptionalResult(
                                        tokens,
                                        start
                                )
        );
    }

    private static ParserToken optionalResult(final List<ParserToken> tokens) {
        return tokens.get(0);
    }

    private static RepeatedParserToken nonOptionalResult(final List<ParserToken> tokens,
                                                         final TextCursorSavePoint start) {
        return RepeatedParserToken.with(
                tokens,
                start.textBetween()
                        .toString()
        );
    }

    // will become public when Parser gains minCount/maxCount

    @Override
    public int minCount() {
        return this.minCount;
    }

    private final int minCount;

    @Override
    public int maxCount() {
        return this.maxCount;
    }

    private final int maxCount;

    private final Parser<C> parser;

    // ParserSetToString................................................................................................

    @Override
    RepeatingParser<C> replaceToString(final String toString) {
        return new RepeatingParser<>(
                this.minCount,
                this.maxCount,
                this.parser,
                toString
        );
    }

    // Object...........................................................................................................

    @Override //
    int hashCode0() {
        return Objects.hash(
                this.minCount,
                this.maxCount
        );
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final RepeatingParser<?> otherRepeatingParser = other.cast();

        return this.minCount == otherRepeatingParser.minCount &&
                this.maxCount == otherRepeatingParser.maxCount;
    }
}
