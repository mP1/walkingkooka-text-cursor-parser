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
import walkingkooka.reflect.PublicStaticHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ParserTokens implements PublicStaticHelper {

    /**
     * {@see BigDecimalParserToken}
     */
    public static BigDecimalParserToken bigDecimal(final BigDecimal value, final String text) {
        return BigDecimalParserToken.with(value, text);
    }

    /**
     * {@see BigIntegerParserToken}
     */
    public static BigIntegerParserToken bigInteger(final BigInteger value, final String text) {
        return BigIntegerParserToken.with(value, text);
    }

    /**
     * {@see CharacterParserToken}
     */
    public static CharacterParserToken character(final char value, final String text) {
        return CharacterParserToken.with(value, text);
    }

    /**
     * {@see DoubleParserToken}
     */
    public static DoubleParserToken doubleParserToken(final double value, final String text) {
        return DoubleParserToken.with(value, text);
    }

    /**
     * {@see DoubleQuotedParserToken}
     */
    public static DoubleQuotedParserToken doubleQuoted(final String value, final String text) {
        return DoubleQuotedParserToken.with(value, text);
    }

    /**
     * {@see FakeParserToken}
     */
    public static FakeParserToken fake() {
        return new FakeParserToken();
    }

    /**
     * {@see LocalDateParserToken}
     */
    public static LocalDateParserToken localDate(final LocalDate value, final String text) {
        return LocalDateParserToken.with(value, text);
    }

    /**
     * {@see LocalDateTimeParserToken}
     */
    public static LocalDateTimeParserToken localDateTime(final LocalDateTime value, final String text) {
        return LocalDateTimeParserToken.with(value, text);
    }

    /**
     * {@see LocalTimeParserToken}
     */
    public static LocalTimeParserToken localTime(final LocalTime value, final String text) {
        return LocalTimeParserToken.with(value, text);
    }

    /**
     * {@see LongParserToken}
     */
    public static LongParserToken longParserToken(final long value, final String text) {
        return LongParserToken.with(value, text);
    }

    /**
     * {@see OffsetDateTimeParserToken}
     */
    public static OffsetDateTimeParserToken offsetDateTime(final OffsetDateTime value, final String text) {
        return OffsetDateTimeParserToken.with(value, text);
    }

    /**
     * {@see OffsetTimeParserToken}
     */
    public static OffsetTimeParserToken offsetTime(final OffsetTime value, final String text) {
        return OffsetTimeParserToken.with(value, text);
    }

    /**
     * {@see RepeatedParserToken}
     */
    public static <T extends ParserToken> RepeatedParserToken repeated(final List<ParserToken> tokens, final String text) {
        return RepeatedParserToken.with(tokens, text);
    }

    /**
     * {@see SequenceParserToken}
     */
    public static SequenceParserToken sequence(final List<ParserToken> tokens, final String text) {
        return SequenceParserToken.with(tokens, text);
    }

    /**
     * {@see SignParserToken}
     */
    public static SignParserToken sign(final boolean value, final String text) {
        return SignParserToken.with(value, text);
    }

    /**
     * {@see SingleQuotedParserToken}
     */
    public static SingleQuotedParserToken singleQuoted(final String value, final String text) {
        return SingleQuotedParserToken.with(value, text);
    }

    /**
     * {@see StringParserToken}
     */
    public static StringParserToken string(final String value, final String text) {
        return StringParserToken.with(value, text);
    }

    /**
     * {@see ZonedDateTimeParserToken}
     */
    public static ZonedDateTimeParserToken zonedDateTime(final ZonedDateTime value, final String text) {
        return ZonedDateTimeParserToken.with(value, text);
    }

    // replaceFirstIf...................................................................................................

    static ParserToken replaceFirstIf(final ParserToken token,
                                      final Predicate<ParserToken> predicate,
                                      final Function<ParserToken, ParserToken> mapper,
                                      final boolean[] stop) {
        ParserToken result = token;

        if (predicate.test(token)) {
            result = mapper.apply(token);
            stop[0] = true;
        } else {
            final List<ParserToken> children = token.children();
            int i = 0;

            for (final ParserToken child : children) {
                final ParserToken childAfter = replaceFirstIf(
                        child,
                        predicate,
                        mapper,
                        stop
                );
                if (stop[0]) {
                    final List<ParserToken> newChildren = Lists.array();
                    newChildren.addAll(
                            children.subList(
                                    0,
                                    i
                            )
                    );
                    newChildren.add(childAfter);
                    newChildren.addAll(
                            children.subList(
                                    i + 1,
                                    children.size()
                            )
                    );
                    result = token.setChildren(newChildren);
                    break;
                }

                i++;
            }
        }

        return result;
    }

    // replaceIf........................................................................................................

    // called only by ParserToken.replaceIf
    static ParserToken replaceIf(final ParserToken token,
                                 final Predicate<ParserToken> predicate,
                                 final Function<ParserToken, ParserToken> mapper) {
        final ParserToken result;

        if (predicate.test(token)) {
            result = mapper.apply(token);
        } else {
            result = token.setChildren(
                    token.children()
                            .stream()
                            .map(
                                    t -> replaceIf(
                                            t,
                                            predicate,
                                            mapper
                                    )
                            ).collect(Collectors.toList())
            );
        }

        return result;
    }

    /**
     * Stop creation
     */
    private ParserTokens() {
        throw new UnsupportedOperationException();
    }
}
