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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class RepeatedOrSequenceParserTokenTestCase<T extends RepeatedOrSequenceParserToken> extends ParserTokenTestCase<T> {

    private final static StringParserToken STRING1 = ParserTokens.string("a1", "a1");
    private final static StringParserToken STRING2 = ParserTokens.string("b2", "b2");
    private final static StringParserToken STRING4 = ParserTokens.string("d4", "d4");
    private final static StringParserToken STRING5 = ParserTokens.string("e5", "e5");
    private final static StringParserToken STRING6 = ParserTokens.string("f6", "f6");

    @Test
    public final void testWithNullTokensFails() {
        assertThrows(NullPointerException.class, () -> this.createToken(null, "tokens"));
    }

    @Test
    public final void testWithZeroTokensFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(Lists.empty(), "abc"));
    }

    // flat............................................................................................................

    @Test
    public final void testFlat() {
        final T token = this.createToken();
        assertSame(token, token.flat());
    }

    @Test
    public final void testFlatRequired() {
        final T child = this.createToken(STRING4, STRING5);
        final T parent = this.createToken(STRING1, STRING2, child);
        final T flat = parent.flat().cast(this.type());
        assertNotSame(parent, flat);
        this.checkEquals(Lists.of(STRING1, STRING2, STRING4, STRING5), flat.value(), "values after flattening");
        this.textAndCheck(flat, "a1b2d4e5");
    }

    @Test
    public final void testFlatRequired2() {
        final T childChild = this.createToken(STRING5, STRING6);
        final T child = this.createToken(STRING4, childChild);
        final T parent = this.createToken(STRING1, STRING2, child);
        final T flat = parent.flat().cast(this.type());
        assertNotSame(parent, flat);
        this.checkEquals(Lists.of(STRING1, STRING2, STRING4, STRING5, STRING6), flat.value(), "values after flattening");
        this.textAndCheck(flat, "a1b2d4e5f6");
    }

    @Test
    public final void testFlatRequired3() {
        final ParserToken parserToken = new FakeParserToken() {
            @Override
            public boolean isSymbol() {
                return false;
            }

            @Override
            public void accept(final ParserTokenVisitor visitor) {
                // nop
            }

            @Override
            public String text() {
                return "!";
            }

            @Override
            public void printTree(final IndentingPrinter printer) {
                printer.println("!");
            }
        };

        final T childChild = this.createToken(STRING5, STRING6, parserToken);
        final T child = this.createToken(STRING4, childChild);
        final T parent = this.createToken(STRING1, STRING2, child);
        final T flat = parent.flat().cast(this.type());
        assertNotSame(parent, flat);
        this.checkEquals(Lists.of(STRING1, STRING2, STRING4, STRING5, STRING6, parserToken), flat.value(), "values after flattening");
        this.textAndCheck(flat, "a1b2d4e5f6!");
    }

    final T createToken(final ParserToken... tokens) {
        return this.createToken(Lists.of(tokens), Arrays.stream(tokens)
                .map(HasText::text)
                .collect(Collectors.joining()));
    }

    final T createToken(final String text, final ParserToken... tokens) {
        return this.createToken(Lists.of(tokens), text);
    }

    abstract T createToken(final List<ParserToken> value, final String text);

    final StringParserToken string(final String s) {
        return ParserTokens.string(s, s);
    }
}
