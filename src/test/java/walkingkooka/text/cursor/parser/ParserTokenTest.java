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
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;

public final class ParserTokenTest implements ClassTesting<ParserToken>, TreePrintableTesting {

    // removeFirstIf....................................................................................................

    @Test
    public void testRemoveFirstIfGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(grandChild2)
                        )
                )
        );
    }

    @Test
    public void testRemoveFirstIfGrandChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild2),
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(grandChild1)
                        )
                )
        );
    }

    @Test
    public void testRemoveFirstIfGreatGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");

        final StringParserToken greatGrandChild1 = stringParserToken("great-grand-child-1");
        final StringParserToken greatGrandChild2 = stringParserToken("great-grand-child-2");
        final StringParserToken greatGrandChild3 = stringParserToken("great-grand-child-3");

        final SequenceParserToken grandChild1 = sequenceParserToken(
                greatGrandChild1,
                greatGrandChild2,
                greatGrandChild3
        );
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");

        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(greatGrandChild2),
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1.setChildren(
                                                Lists.of(
                                                        greatGrandChild1,
                                                        greatGrandChild3
                                                )
                                        ),
                                        grandChild2
                                )
                        )
                )
        );
    }

    @Test
    public void testRemoveFirstIfStopsRemovingAfterFirst() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.always(),
                sequenceParserToken(
                        child2
                )
        );
    }

    private void removeFirstIfAndCheck(final ParserToken token,
                                       final Predicate<ParserToken> predicate,
                                       final ParserToken expected) {
        this.checkEquals(
                expected,
                token.removeFirstIf(predicate),
                () -> token + " removeFirstIf " + predicate
        );
    }

    // removeIf.........................................................................................................

    @Test
    public void testRemoveIfGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(grandChild2)
                        )
                )
        );
    }

    @Test
    public void testRemoveIfGrandChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild2),
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(grandChild1)
                        )
                )
        );
    }

    @Test
    public void testRemoveIfGreatGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");

        final StringParserToken greatGrandChild1 = stringParserToken("great-grand-child-1");
        final StringParserToken greatGrandChild2 = stringParserToken("great-grand-child-2");
        final StringParserToken greatGrandChild3 = stringParserToken("great-grand-child-3");

        final SequenceParserToken grandChild1 = sequenceParserToken(
                greatGrandChild1,
                greatGrandChild2,
                greatGrandChild3
        );
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");

        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(greatGrandChild2),
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1.setChildren(
                                                Lists.of(
                                                        greatGrandChild1,
                                                        greatGrandChild3
                                                )
                                        ),
                                        grandChild2
                                )
                        )
                )
        );
    }

    @Test
    public void testRemoveIfMany() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                t -> t == child1 || t == grandChild2,
                sequenceParserToken(
                        child2.setChildren(
                                Lists.of(
                                        grandChild1
                                )
                        )
                )
        );
    }

    @Test
    public void testRemoveIfMany2() {
        final StringParserToken child1 = stringParserToken("child-1");

        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken grandChild3 = stringParserToken("grand-child-3");
        final StringParserToken grandChild4 = stringParserToken("grand-child-4");
        final SequenceParserToken child3 = sequenceParserToken(
                grandChild3,
                grandChild4
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                t -> t == grandChild2 || t == child3,
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1
                                )
                        )
                )
        );
    }

    private void removeIfAndCheck(final ParserToken token,
                                  final Predicate<ParserToken> predicate,
                                  final ParserToken expected) {
        this.checkEquals(
                expected,
                token.removeIf(predicate),
                () -> token + " removeIf " + predicate
        );
    }

    // removeFirstIf & removeIf helpers.................................................................................

    private StringParserToken stringParserToken(final String text) {
        return ParserTokens.string(text, text);
    }

    private SequenceParserToken sequenceParserToken(final ParserToken... children) {
        return sequenceParserToken(
                Lists.of(children)
        );
    }

    private SequenceParserToken sequenceParserToken(final List<ParserToken> children) {
        return ParserTokens.sequence(
                children,
                ParserToken.text(children)
        );
    }

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
