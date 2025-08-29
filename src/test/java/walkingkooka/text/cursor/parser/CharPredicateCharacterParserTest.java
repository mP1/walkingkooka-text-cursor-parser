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
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.cursor.TextCursor;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CharPredicateCharacterParserTest extends NonEmptyParserTestCase<CharPredicateCharacterParser<ParserContext>, CharacterParserToken>
    implements HashCodeEqualsDefinedTesting2<CharPredicateCharacterParser<ParserContext>> {

    private final static CharPredicate DIGITS = CharPredicates.digit();

    @Test
    public void testWithNullCharPredicateFails() {
        assertThrows(
            NullPointerException.class,
            () -> CharPredicateCharacterParser.with(null)
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseFailure() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testParseFailure2() {
        this.parseFailAndCheck("abc");
    }

    @Test
    public void testParseSuccess() {
        this.parseAndCheck2("1", '1', "1");
    }

    @Test
    public void testParseSuccess2() {
        this.parseAndCheck2("2", '2', "2");
    }

    @Test
    public void testParseSuccess3() {
        this.parseAndCheck2("2abc", '2', "2", "abc");
    }

    @Test
    public void testParseMultiple() {
        final TextCursor cursor = this.parseAndCheck2("123abc", '1', "1", "23abc");
        this.parseAndCheck2(cursor, '2', "2", "3abc");
        this.parseAndCheck2(cursor, '3', "3", "abc");
    }

    @Override
    public CharPredicateCharacterParser<ParserContext> createParser() {
        return CharPredicateCharacterParser.with(DIGITS);
    }

    private TextCursor parseAndCheck2(final String in,
                                      final char value,
                                      final String text) {
        return this.parseAndCheck2(
            in,
            value,
            text,
            ""
        );
    }

    private TextCursor parseAndCheck2(final String in,
                                      final char value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck(
            in,
            CharacterParserToken.with(value, text),
            text,
            textAfter
        );
    }

    private TextCursor parseAndCheck2(final TextCursor cursor,
                                      final char value,
                                      final String text,
                                      final String textAfter) {
        return this.parseAndCheck(
            cursor,
            CharacterParserToken.with(value, text),
            text,
            textAfter
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentCharPredicate() {
        this.checkNotEquals(
            CharPredicateCharacterParser.with(
                CharPredicates.fake()
            ),
            CharPredicateCharacterParser.with(
                CharPredicates.fake()
            )
        );
    }

    @Override
    public CharPredicateCharacterParser<ParserContext> createObject() {
        return this.createParser();
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), DIGITS.toString());
    }

    // Class............................................................................................................

    @Override
    public Class<CharPredicateCharacterParser<ParserContext>> type() {
        return Cast.to(CharPredicateCharacterParser.class);
    }
}
