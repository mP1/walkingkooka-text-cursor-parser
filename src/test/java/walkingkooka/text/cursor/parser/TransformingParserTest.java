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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;

import java.math.BigInteger;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransformingParserTest extends ParserWrapperTestCase<TransformingParser<ParserContext>>
        implements HashCodeEqualsDefinedTesting2<TransformingParser<ParserContext>> {

    private final static int RADIX = 10;
    private final static Parser<ParserContext> WRAPPED_PARSER = Parsers.stringCharPredicate(CharPredicates.digit(), 1, 10);
    private final static BiFunction<ParserToken, ParserContext, ParserToken> TRANSFORMER = (t, c) -> ParserTokens.bigInteger(new BigInteger(((StringParserToken) t).value(), RADIX), t.text());

    @Test
    public void testWithNullTransformerFails() {
        assertThrows(NullPointerException.class, () -> TransformingParser.with(WRAPPED_PARSER, null));
    }

    @Test
    public void testFailure() {
        this.parseFailAndCheck("a");
    }

    @Test
    public void testFailure2() {
        this.parseFailAndCheck("abc");
    }

    @Test
    public void testSuccessEoc() {
        this.parseAndCheck2("1", 1, "1", "");
    }

    @Test
    public void testSuccessEoc2() {
        this.parseAndCheck2("123", 123, "123", "");
    }

    @Test
    public void testSuccessTokenAfter() {
        this.parseAndCheck2("123abc", 123, "123", "abc");
    }

    @Test
    public void testDefaultMethodTransform() {
        this.parseAndCheck4(WRAPPED_PARSER.transform(TRANSFORMER), "123abc", 123, "123", "abc");
    }

    // equals/hashCode..................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
                TransformingParser.with(
                        Parsers.fake(),
                        TRANSFORMER
                )
        );
    }

    @Test
    public void testEqualsDifferentTransformer() {
        this.checkNotEquals(
                TransformingParser.with(
                        WRAPPED_PARSER,
                        (i, ii) -> {
                            throw new UnsupportedOperationException();
                        }
                )
        );
    }

    @Override
    public TransformingParser<ParserContext> createObject() {
        return this.createParser();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), WRAPPED_PARSER.toString());
    }

    // parse............................................................................................................

    @Override
    TransformingParser<ParserContext> createParser(final Parser<ParserContext> parser) {
        return TransformingParser.with(
                parser,
                TRANSFORMER
        );
    }

    @Override
    Parser<ParserContext> wrappedParser() {
        return WRAPPED_PARSER;
    }

    private TextCursor parseAndCheck2(final String in, final long value, final String text, final String textAfter) {
        return this.parseAndCheck3(in, value, text, textAfter);
    }

    private TextCursor parseAndCheck3(final String from, final long value, final String text, final String textAfter) {
        return this.parseAndCheck4(this.createParser(),
                from,
                value,
                text,
                textAfter);
    }

    private TextCursor parseAndCheck4(final Parser<ParserContext> parser, final String from, final long value, final String text, final String textAfter) {
        return this.parseAndCheck(parser,
                this.createContext(),
                TextCursors.charSequence(from),
                ParserTokens.bigInteger(BigInteger.valueOf(value), text),
                text,
                textAfter);
    }

    // Class............................................................................................................

    @Override
    public Class<TransformingParser<ParserContext>> type() {
        return Cast.to(TransformingParser.class);
    }
}
