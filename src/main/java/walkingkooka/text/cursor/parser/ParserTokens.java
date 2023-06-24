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
import java.util.function.Predicate;

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

    // ParserToken.parentRemoveFirstIf...unfortunately GWT does not support private default methods on an interface (J2CL doesnt complain).

    static ParserToken parentRemoveFirstIf(final ParserToken parent,
                                           final Predicate<ParserToken> predicate,
                                           final boolean[] removed) {
        ParserToken result = parent;
        int i = 0;

        final List<ParserToken> children = parent.children();
        for (final ParserToken child : children) {
            if (predicate.test(child)) {
                final List<ParserToken> without = Lists.array();
                without.addAll(children.subList(0, i));
                without.addAll(children.subList(i + 1, children.size()));

                result = parent.setChildren(without);
                removed[0] = true;
                break;
            }

            if (child.isParent()) {
                final ParserToken childResult = parentRemoveFirstIf(
                        child,
                        predicate,
                        removed
                );

                if (removed[0]) {
                    final List<ParserToken> without = Lists.array();
                    without.addAll(children.subList(0, i));
                    without.add(childResult);
                    without.addAll(children.subList(i + 1, children.size()));

                    result = parent.setChildren(without);
                    break;
                }
            }

            i++;
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
