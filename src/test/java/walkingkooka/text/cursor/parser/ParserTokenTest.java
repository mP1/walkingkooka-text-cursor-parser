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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.LinkedHashSet;
import java.util.List;

public final class ParserTokenTest implements ClassTesting<ParserToken> {

    // collect..........................................................................................................

    @Test
    public void testCollectLeaf() {
        final StringParserToken parserToken = ParserTokens.string("Hello", "Goodbye");
        this.collectAndCheck(
                parserToken,
                parserToken
        );
    }

    @Test
    public void testCollectParent() {
        final StringParserToken child = ParserTokens.string("Hello", "Goodbye");
        final RepeatedParserToken parent = ParserTokens.repeated(
                Lists.of(
                        child
                ),
                "Dont care"
        );
        this.collectAndCheck(
                parent,
                parent,
                child
        );
    }

    @Test
    public void testCollectParent2() {
        final StringParserToken child1 = ParserTokens.string("Hello1", "Goodbye1");
        final StringParserToken child2 = ParserTokens.string("Hello2", "Goodbye2");

        final RepeatedParserToken parent = ParserTokens.repeated(
                Lists.of(
                        child1,
                        child2
                ),
                "Dont care"
        );
        this.collectAndCheck(
                parent,
                parent,
                child1,
                child2
        );
    }

    @Test
    public void testCollectParent3() {
        final StringParserToken child1 = ParserTokens.string("Hello1", "Goodbye1");

        final RepeatedParserToken parent1 = ParserTokens.repeated(
                Lists.of(
                        child1
                ),
                "Dont care"
        );
        final StringParserToken child2 = ParserTokens.string("Hello2", "Goodbye2");
        final RepeatedParserToken parent2 = ParserTokens.repeated(
                Lists.of(
                        parent1,
                        child2
                ),
                "Dont care"
        );
        this.collectAndCheck(
                parent2,
                parent2,
                parent1,
                child1,
                child2
        );
    }

    @Test
    public void testCollectParent4() {
        final StringParserToken child1 = ParserTokens.string("Hello1", "Goodbye1");

        final RepeatedParserToken parent1 = ParserTokens.repeated(
                Lists.of(
                        child1
                ),
                "Dont care"
        );
        final StringParserToken child2 = ParserTokens.string("Hello2", "Goodbye2");
        final RepeatedParserToken parent2 = ParserTokens.repeated(
                Lists.of(
                        child2,
                        parent1
                ),
                "Dont care"
        );
        this.collectAndCheck(
                parent2,
                parent2,
                child2,
                parent1,
                child1
        );
    }

    private void collectAndCheck(final ParserToken token,
                                 final ParserToken... expected) {
        this.collectAndCheck(
                token,
                Lists.of(expected)
        );
    }

    private void collectAndCheck(final ParserToken token,
                                 final List<ParserToken> expected) {
        this.checkNotEquals(
                Lists.empty(),
                expected
        );

        this.checkEquals(
                token,
                expected.get(0),
                () -> "Expected first to be starting token"
        );

        this.checkEquals(
                expected,
                Lists.of(new LinkedHashSet<>(expected).toArray()),
                () -> "Expected no duplicates"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<ParserToken> type() {
        return ParserToken.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
