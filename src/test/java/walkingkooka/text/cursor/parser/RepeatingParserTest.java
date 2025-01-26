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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CaseSensitivity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepeatingParserTest extends NonEmptyParserTestCase<RepeatingParser<ParserContext>,
        RepeatedParserToken> implements HashCodeEqualsDefinedTesting2<RepeatingParser<ParserContext>> {

    private final static int MIN_COUNT = 3;
    private final static int MAX_COUNT = 4;

    private final static String TEXT = "abc";
    private final static Parser<ParserContext> PARSER = Parsers.string(
            TEXT,
            CaseSensitivity.SENSITIVE
    );

    @Test
    public void testWithInvalidMinCountFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> RepeatingParser.with(
                        -1,
                        MAX_COUNT,
                        PARSER
                )
        );

        this.checkEquals(
                "Invalid min count -1 < 0",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithInvalidMaxCountFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> RepeatingParser.with(
                        3,
                        2,
                        PARSER
                )
        );

        this.checkEquals(
                "Invalid max count 2 < min count 3",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithZeroMinCountAndMaxCountFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> RepeatingParser.with(
                        0,
                        0,
                        PARSER
                )
        );

        this.checkEquals(
                "Invalid min count 0 and max count 0",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithNullParserFails() {
        assertThrows(
                NullPointerException.class,
                () -> RepeatingParser.with(
                        MIN_COUNT,
                        MAX_COUNT,
                        null
                )
        );
    }

    @Test
    public void testWithMinMaxBothOne() {
        assertSame(
                PARSER,
                RepeatingParser.with(
                        1,
                        1,
                        PARSER
                )
        );
    }

    @Test
    public void testWithSameRepeatedParserZeroAndMaxInteger() {
        final Parser<ParserContext> parser = RepeatingParser.with(
                RepeatingParser.DEFAULT_REPEAT_MIN_COUNT,
                RepeatingParser.DEFAULT_REPEAT_MAX_COUNT,
                PARSER
        );
        assertSame(
                parser,
                RepeatingParser.with(
                        RepeatingParser.DEFAULT_REPEAT_MIN_COUNT,
                        RepeatingParser.DEFAULT_REPEAT_MAX_COUNT,
                        parser
                )
        );
    }

    @Test
    public void testWithSameRepeatedParser() {
        final RepeatingParser<ParserContext> parser = this.createParser();
        this.checkEquals(
                RepeatingParser.with(
                        MIN_COUNT * MIN_COUNT,
                        MAX_COUNT * MAX_COUNT,
                        PARSER
                ),
                RepeatingParser.with(
                        parser.minCount(),
                        parser.maxCount(),
                        parser.cast()
                )
        );
    }

    @Test
    public void testWithParserSameMinCountSameMaxCount() {
        assertSame(
                PARSER,
                RepeatingParser.with(
                        PARSER.minCount(),
                        PARSER.maxCount(),
                        PARSER
                )
        );
    }

    @Test
    public void testWithParserSameMinCountSameMaxCount2() {
        final Parser<ParserContext> parser = new FakeParser<>() {
            @Override
            public int minCount() {
                return 3;
            }

            @Override
            public int maxCount() {
                return 4;
            }
        };

        assertSame(
                parser,
                RepeatingParser.with(
                        parser.minCount(),
                        parser.maxCount(),
                        parser
                )
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseIncomplete() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseIncomplete2() {
        this.parseFailAndCheck("ab");
    }

    @Test
    public void testParseLessThanMinCount() {
        this.parseFailAndCheck(
                TEXT
        );
    }

    @Test
    public void testParseLessThanMinCount2() {
        this.parseFailAndCheck(
                TEXT + TEXT
        );
    }

    @Test
    public void testParseMinCount() {
        final String text = TEXT + TEXT + TEXT;

        this.parseAndCheck(
                text,
                RepeatedParserToken.with(
                        Lists.of(
                                string(TEXT),
                                string(TEXT),
                                string(TEXT)
                        ),
                        text
                ),
                text,
                ""
        );
    }

    @Test
    public void testParseMinCountIncompleteIgnored() {
        final String after = TEXT.substring(0, 1);
        final String text = TEXT + TEXT + TEXT;

        this.parseAndCheck(
                text + after,
                RepeatedParserToken.with(
                        Lists.of(
                                string(TEXT),
                                string(TEXT),
                                string(TEXT)
                        ),
                        text
                ),
                text,
                after
        );
    }

    @Test
    public void testParseMaxCount() {
        final String text = TEXT + TEXT + TEXT + TEXT;

        this.parseAndCheck(
                text,
                RepeatedParserToken.with(
                        Lists.of(
                                string(TEXT),
                                string(TEXT),
                                string(TEXT),
                                string(TEXT)
                        ),
                        text
                ),
                text,
                ""
        );
    }

    @Test
    public void testParseMaxCountExtraTokenIgnored() {
        final String text = TEXT + TEXT + TEXT + TEXT;
        final String after = TEXT;

        this.parseAndCheck(
                text + after,
                RepeatedParserToken.with(
                        Lists.of(
                                string(TEXT),
                                string(TEXT),
                                string(TEXT),
                                string(TEXT)
                        ),
                        text
                ),
                text,
                after
        );
    }

    @Test
    public void testParseMaxCountExtraTokenIgnored2() {
        final String text = TEXT + TEXT + TEXT + TEXT;
        final String after = "!!!";

        this.parseAndCheck(
                text + after,
                RepeatedParserToken.with(
                        Lists.of(
                                string(TEXT),
                                string(TEXT),
                                string(TEXT),
                                string(TEXT)
                        ),
                        text
                ),
                text,
                after
        );
    }

    @Test
    public void testParseMinCount0AndMaxCount2() {
        final String text = TEXT + TEXT;
        final String after = "!!!";

        this.parseAndCheck(
                RepeatingParser.with(
                        0, // min
                        2, // max
                        PARSER
                ),
                text + after,
                RepeatedParserToken.with(
                        Lists.of(
                                string(TEXT),
                                string(TEXT)
                        ),
                        text
                ),
                text,
                after
        );
    }

    @Test
    public void testParseOptionalMinCount0AndMaxCount1() {
        final String text = TEXT;
        final String after = "!!!";

        this.parseAndCheck(
                RepeatingParser.with(
                        0, // min
                        1, // max
                        PARSER
                ),
                text + after,
                string(TEXT),
                text,
                after
        );
    }

    @Test
    public void testParseOptionalMinCount0AndMaxCount1SecondCopyIgnored() {
        final String text = TEXT;
        final String after = TEXT;

        this.parseAndCheck(
                RepeatingParser.with(
                        0, // min
                        1, // max
                        PARSER
                ),
                text + after,
                string(TEXT),
                text,
                after
        );
    }

    @Test
    @Override
    public void testMinCount() {
        this.minCountAndCheck(
                this.createParser(),
                MIN_COUNT
        );
    }

    @Test
    @Override
    public void testMaxCount() {
        this.maxCountAndCheck(
                this.createParser(),
                MAX_COUNT
        );
    }

    @Test
    public void testOptionalWhenOptional() {
        final RepeatingParser<ParserContext> repeating = Cast.to(
                RepeatingParser.with(
                        0,
                        100,
                        PARSER
                )
        );

        this.optionalAndCheck(
                repeating,
                repeating
        );
    }

    @Test
    public void testOptionalWhenNotOptional() {
        final RepeatingParser<ParserContext> repeating = Cast.to(
                RepeatingParser.with(
                        1,
                        100,
                        PARSER
                )
        );

        this.optionalAndCheck(
                repeating,
                RepeatingParser.with(
                        0,
                        100,
                        PARSER
                )
        );
    }

    @Override
    public RepeatingParser<ParserContext> createParser() {
        return Cast.to(
                RepeatingParser.with(
                        MIN_COUNT,
                        MAX_COUNT,
                        PARSER
                )
        );
    }

    private static StringParserToken string(final String s) {
        return ParserTokens.string(s, s);
    }

    // hashCode/equals...................................................................................................

    @Test
    public void testEqualsMinCount() {
        this.checkNotEquals(
                RepeatingParser.with(
                        1,
                        MAX_COUNT,
                        PARSER
                ),
                RepeatingParser.with(
                        2,
                        MAX_COUNT,
                        PARSER
                )
        );
    }

    @Test
    public void testEqualsMaxCount() {
        this.checkNotEquals(
                RepeatingParser.with(
                        MIN_COUNT,
                        MAX_COUNT,
                        PARSER
                ),
                RepeatingParser.with(
                        MIN_COUNT,
                        MAX_COUNT + 1,
                        PARSER
                )
        );
    }

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
                RepeatingParser.with(
                        MIN_COUNT,
                        MAX_COUNT,
                        new FakeParser() {
                            @Override
                            public int minCount() {
                                return 1;
                            }

                            @Override
                            public int maxCount() {
                                return 1;
                            }
                        }
                )
        );
    }

    @Test
    public void testEqualsDifferentToString() {
        this.checkNotEquals(
                RepeatingParser.with(
                        MIN_COUNT,
                        MAX_COUNT,
                        PARSER
                ).setToString("different to string")
        );
    }

    @Override
    public RepeatingParser<ParserContext> createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToStringZeroMinCountIntegerOneMaxCount() {
        this.toStringAndCheck(
                RepeatingParser.with(
                        0,
                        1,
                        PARSER
                ),
                "[" + PARSER + "]"
        );
    }

    @Test
    public void testToStringZeroMinCountIntegerMaxMaxCount() {
        this.toStringAndCheck(
                RepeatingParser.with(
                        0,
                        Integer.MAX_VALUE,
                        PARSER
                ),
                "{" + PARSER + "}"
        );
    }

    @Test
    public void testToStringOneMinCountIntegerMaxMaxCount() {
        this.toStringAndCheck(
                RepeatingParser.with(
                        1,
                        Integer.MAX_VALUE,
                        PARSER
                ),
                "{" + PARSER + "}{1,*}"
        );
    }

    @Test
    public void testToStringNonZeroMinCountEqMaxCount() {
        this.toStringAndCheck(
                RepeatingParser.with(
                        2,
                        2,
                        PARSER
                ),
                "{" + PARSER + "}{2}"
        );
    }

    @Test
    public void testToStringNonZeroMinCountIntegerMaxMaxCount() {
        this.toStringAndCheck(
                RepeatingParser.with(
                        3,
                        Integer.MAX_VALUE,
                        PARSER
                ),
                "{" + PARSER + "}{3,*}"
        );
    }

    @Test
    public void testToStringNonZeroMinCountNonZeroMaxCount() {
        this.toStringAndCheck(
                RepeatingParser.with(
                        3,
                        4,
                        PARSER
                ),
                "{" + PARSER + "}{3,4}"
        );
    }


    @Test
    public void testToStringZeroMinCountMaxCount() {
        this.toStringAndCheck(
                this.createParser(),
                "{" + PARSER + "}{3,4}"
        );
    }

    // class............................................................................................................

    @Override
    public Class<RepeatingParser<ParserContext>> type() {
        return Cast.to(RepeatingParser.class);
    }
}
