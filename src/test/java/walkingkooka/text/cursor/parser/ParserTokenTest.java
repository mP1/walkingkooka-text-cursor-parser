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

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ParserTokenTest implements ClassTesting<ParserToken>, TreePrintableTesting {

    // findFirst........................................................................................................

    @Test
    public void testFindFirstWithNullPredicateFails() {
        assertThrows(
                NullPointerException.class,
                () -> stringParserToken("Hello")
                        .findFirst(null)
        );
    }

    @Test
    public void testFindFirstNever() {
        this.findFirstAndCheck(
                stringParserToken("Hello"),
                Predicates.never()
        );
    }

    @Test
    public void testFindFirstThis() {
        final StringParserToken token = stringParserToken("Hello");
        this.findFirstAndCheck(
                token,
                Predicates.always(),
                token
        );
    }

    @Test
    public void testFindFirstSkipsRemaining() {
        final StringParserToken matched = stringParserToken("Hello");
        final ParserToken fake = new FakeParserToken() {
            @Override
            public Optional<ParserToken> findFirst(final Predicate<ParserToken> predicate) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String text() {
                return "fake";
            }
        };

        this.findFirstAndCheck(
                sequenceParserToken(
                        matched,
                        fake
                ),
                Predicates.is(matched),
                matched
        );
    }

    @Test
    public void testFindFirstGraphNever() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findFirstAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                Predicates.never()
        );
    }

    @Test
    public void testFindFirstGraph() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findFirstAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                Predicates.is(grandChild1),
                grandChild1
        );
    }

    @Test
    public void testFindFirstGraph2() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findFirstAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                (t) -> t.isLeaf() && t.text().contains("grand"),
                grandChild1
        );
    }

    private void findFirstAndCheck(final ParserToken start,
                                   final Predicate<ParserToken> predicate) {
        this.findFirstAndCheck(
                start,
                predicate,
                Optional.empty()
        );
    }

    private void findFirstAndCheck(final ParserToken start,
                                   final Predicate<ParserToken> predicate,
                                   final ParserToken expected) {
        this.findFirstAndCheck(
                start,
                predicate,
                Optional.of(expected)
        );
    }

    private void findFirstAndCheck(final ParserToken start,
                                   final Predicate<ParserToken> predicate,
                                   final Optional<ParserToken> expected) {
        this.checkEquals(
                expected,
                start.findFirst(predicate),
                () -> start + " findFirst " + predicate
        );
    }

    // findIf.............................................................................................................

    @Test
    public void testFindIfWithNullPredicateFails() {
        assertThrows(
                NullPointerException.class,
                () -> stringParserToken("Hello")
                        .findIf(
                                null,
                                (t) -> {
                                }
                        )
        );
    }

    @Test
    public void testFindIfWithNullConsumerFails() {
        assertThrows(
                NullPointerException.class,
                () -> stringParserToken("Hello")
                        .findIf(
                                Predicates.fake(),
                                null
                        )
        );
    }

    @Test
    public void testFindIfNever() {
        this.findIfAndCheck(
                stringParserToken("Hello"),
                Predicates.never()
        );
    }

    @Test
    public void testFindIfGraphNever() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                Predicates.never()
        );
    }

    @Test
    public void testFindIfGraph() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                Predicates.is(grandChild1),
                grandChild1
        );
    }

    @Test
    public void testFindIfGraph2() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                (t) -> t.isLeaf() && t.text().contains("grand"),
                grandChild1,
                grandChild2
        );
    }

    @Test
    public void testFindIfGraph3() {
        final ParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = stringParserToken("child-2");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");
        final ParserToken child3 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.findIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                (t) -> t.isLeaf() && t.text().contains("1"),
                child1,
                grandChild1
        );
    }

    private void findIfAndCheck(final ParserToken start,
                                final Predicate<ParserToken> predicate,
                                final ParserToken... expected) {
        final List<ParserToken> consumer = Lists.array();
        start.findIf(
                predicate,
                consumer::add
        );
        this.checkEquals(
                Lists.of(expected),
                consumer,
                () -> start + " findIf " + predicate
        );
    }

    // removeFirstIf....................................................................................................

    @Test
    public void testRemoveFirstIfFirstChildOnlyGrandChild() {
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final SequenceParserToken child1 = sequenceParserToken(
                grandChild1
        );

        final StringParserToken child2 = stringParserToken("child-1");

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                sequenceParserToken(
                        child2
                )
        );
    }

    @Test
    public void testRemoveFirstIfLastChildOnlyGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");

        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1
        );

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                sequenceParserToken(
                        child1
                )
        );
    }

    @Test
    public void testRemoveFirstIfFirstChildGrandChild1() {
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child1 = sequenceParserToken(
                grandChild1,
                grandChild2
        );
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                sequenceParserToken(
                        child1.setChildren(
                                Lists.of(grandChild2)
                        ),
                        child2
                )
        );
    }

    @Test
    public void testRemoveFirstIfFirstChildGrandChild2() {
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child1 = sequenceParserToken(
                grandChild1,
                grandChild2
        );
        final StringParserToken child2 = stringParserToken("child-1");

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild2),
                sequenceParserToken(
                        child1.setChildren(
                                Lists.of(grandChild1)
                        ),
                        child2
                )
        );
    }

    @Test
    public void testRemoveFirstIfLastChildGrandChild1() {
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
    public void testRemoveFirstIfLastChildGrandChild2() {
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
    public void testRemoveFirstIfStopsRemovingAfterThis() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.always()
        );
    }

    @Test
    public void testRemoveFirstIfStopsRemovingAfterFirstChild1() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                t -> t.equals(child1) || t.equals(child2),
                sequenceParserToken(
                        child2
                )
        );
    }

    @Test
    public void testRemoveFirstIfStopsRemovingAfterFirstChild2() {
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
                t -> t.equals(grandChild1) || t.equals(grandChild2),
                sequenceParserToken(
                        child1,
                        sequenceParserToken(
                                grandChild2
                        )
                )
        );
    }

    private void removeFirstIfAndCheck(final ParserToken token,
                                       final Predicate<ParserToken> predicate) {
        this.removeFirstIfAndCheck(
                token,
                predicate,
                Optional.empty()
        );
    }

    private void removeFirstIfAndCheck(final ParserToken token,
                                       final Predicate<ParserToken> predicate,
                                       final ParserToken expected) {
        this.removeFirstIfAndCheck(
                token,
                predicate,
                Optional.of(expected)
        );
    }

    private void removeFirstIfAndCheck(final ParserToken token,
                                       final Predicate<ParserToken> predicate,
                                       final Optional<ParserToken> expected) {
        this.checkEquals(
                expected,
                token.removeFirstIf(predicate),
                () -> token + " removeFirstIf " + predicate
        );
    }

    // removeIf.........................................................................................................

    @Test
    public void testRemoveIfFirstChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child1),
                sequenceParserToken(
                        child2
                )
        );
    }

    @Test
    public void testRemoveIfFirstChild2() {
        final ParserToken child1 = sequenceParserToken(
                stringParserToken("child-1")
        );
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child1),
                sequenceParserToken(
                        child2
                )
        );
    }

    @Test
    public void testRemoveIfLastChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child2),
                sequenceParserToken(
                        child1
                )
        );
    }

    @Test
    public void testRemoveIfLastChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final ParserToken child2 = sequenceParserToken(
                stringParserToken("child-2")
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child2),
                sequenceParserToken(
                        child1
                )
        );
    }

    @Test
    public void testRemoveIfAllChildren() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                t -> t.equals(child1) || t.equals(child2)
        );
    }

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
    public void testRemoveIfGreatGrandChildren() {
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
                t -> t.equals(greatGrandChild1) || t.equals(greatGrandChild2) || t.equals(greatGrandChild3),
                sequenceParserToken(
                        child1,
                        sequenceParserToken(
                                grandChild2
                        )
                )
        );
    }

    @Test
    public void testRemoveIfAllGrandChildren() {
        this.removeIfAndCheck(
                sequenceParserToken(
                        sequenceParserToken(
                                stringParserToken("grand-child-1"),
                                stringParserToken("grand-child-2")
                        )
                ),
                ParserToken::isLeaf
        );
    }

    @Test
    public void testRemoveIfLeaf() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                ParserToken::isLeaf
        );
    }

    @Test
    public void testRemoveIfLeaf2() {
        final StringParserToken child1 = stringParserToken("child-1");

        final ParserToken grandChild1 = stringParserToken("grand-child-1");
        final ParserToken grandChild2 = stringParserToken("grand-child-2");

        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        this.removeIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                ParserToken::isLeaf
        );
    }

    @Test
    public void testRemoveIfLeaf3() {
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
                ParserToken::isLeaf
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
                                  final Predicate<ParserToken> predicate) {
        this.removeIfAndCheck(
                token,
                predicate,
                Optional.empty()
        );
    }

    private void removeIfAndCheck(final ParserToken token,
                                  final Predicate<ParserToken> predicate,
                                  final ParserToken expected) {
        this.removeIfAndCheck(
                token,
                predicate,
                Optional.of(expected)
        );
    }

    private void removeIfAndCheck(final ParserToken token,
                                  final Predicate<ParserToken> predicate,
                                  final Optional<ParserToken> expected) {
        this.checkEquals(
                expected,
                token.removeIf(predicate),
                () -> token + " removeIf " + predicate
        );
    }

    // replaceFirstIf...................................................................................................

    @Test
    public void testReplaceFirstIfChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child1),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        replacement,
                        child2
                )
        );
    }

    @Test
    public void testReplaceFirstIfChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        replacement
                )
        );
    }

    @Test
    public void testReplaceFirstIfParent() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        replacement
                )
        );
    }

    @Test
    public void testReplaceFirstIfGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        replacement,
                                        grandChild2
                                )
                        )
                )
        );
    }

    @Test
    public void testReplaceFirstIfGrandChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1,
                                        replacement
                                )
                        )
                )
        );
    }

    @Test
    public void testReplaceFirstIfGreatGrandChild() {
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

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(greatGrandChild2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1.setChildren(
                                                Lists.of(
                                                        greatGrandChild1,
                                                        replacement,
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
    public void testReplaceFirstIfMany() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                t -> t == child1 || t == grandChild2,
                (t) -> replacement, // mapper
                sequenceParserToken(
                        replacement,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1,
                                        grandChild2
                                )
                        )
                )
        );
    }

    @Test
    public void testReplaceFirstIfMany2() {
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

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceFirstIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                t -> t == grandChild2 || t == child3,
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1,
                                        replacement
                                )
                        ),
                        child3
                )
        );
    }

    private void replaceFirstIfAndCheck(final ParserToken token,
                                        final Predicate<ParserToken> predicate,
                                        final Function<ParserToken, ParserToken> mapper,
                                        final ParserToken expected) {
        this.checkEquals(
                expected,
                token.replaceFirstIf(
                        predicate,
                        mapper
                ),
                () -> token + " replaceFirstIf " + predicate + "," + mapper
        );
    }

    // replaceIf........................................................................................................

    @Test
    public void testReplaceIfChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child1),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        replacement,
                        child2
                )
        );
    }

    @Test
    public void testReplaceIfChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken child2 = stringParserToken("child-2");

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        replacement
                )
        );
    }

    @Test
    public void testReplaceIfParent() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(child2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        replacement
                )
        );
    }

    @Test
    public void testReplaceIfGrandChild() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild1),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        replacement,
                                        grandChild2
                                )
                        )
                )
        );
    }

    @Test
    public void testReplaceIfGrandChild2() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(grandChild2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1,
                                        replacement
                                )
                        )
                )
        );
    }

    @Test
    public void testReplaceIfGreatGrandChild() {
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

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                Predicates.is(greatGrandChild2),
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1.setChildren(
                                                Lists.of(
                                                        greatGrandChild1,
                                                        replacement,
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
    public void testReplaceIfMany() {
        final StringParserToken child1 = stringParserToken("child-1");
        final StringParserToken grandChild1 = stringParserToken("grand-child-1");
        final StringParserToken grandChild2 = stringParserToken("grand-child-2");
        final SequenceParserToken child2 = sequenceParserToken(
                grandChild1,
                grandChild2
        );

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2
                ),
                t -> t == child1 || t == grandChild2,
                (t) -> replacement, // mapper
                sequenceParserToken(
                        replacement,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1,
                                        replacement
                                )
                        )
                )
        );
    }

    @Test
    public void testReplaceIfMany2() {
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

        final StringParserToken replacement = stringParserToken("REPLACEMENT");

        this.replaceIfAndCheck(
                sequenceParserToken(
                        child1,
                        child2,
                        child3
                ),
                t -> t == grandChild2 || t == child3,
                (t) -> replacement, // mapper
                sequenceParserToken(
                        child1,
                        child2.setChildren(
                                Lists.of(
                                        grandChild1,
                                        replacement
                                )
                        ),
                        replacement
                )
        );
    }

    private void replaceIfAndCheck(final ParserToken token,
                                   final Predicate<ParserToken> predicate,
                                   final Function<ParserToken, ParserToken> mapper,
                                   final ParserToken expected) {
        this.checkEquals(
                expected,
                token.replaceIf(
                        predicate,
                        mapper
                ),
                () -> token + " replaceIf " + predicate + "," + mapper
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

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintBigDecimal() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        new BigDecimal("123.5"),
                        "123.5"
                ),
                "TestLeaf \"123.5\" 123.5 (java.math.BigDecimal)\n"
        );
    }

    @Test
    public void testTreePrintBoolean() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        true,
                        "true"
                ),
                "TestLeaf \"true\" true\n"
        );
    }

    @Test
    public void testTreePrintCharacter() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        'A',
                        "A"
                ),
                "TestLeaf \"A\" 'A'\n"
        );
    }

    @Test
    public void testTreePrintDouble() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        1.5,
                        "1.5"
                ),
                "TestLeaf \"1.5\" 1.5\n"
        );
    }

    @Test
    public void testTreePrintFloat() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        1.25f,
                        "1.25"
                ),
                "TestLeaf \"1.25\" 1.25F\n"
        );
    }

    @Test
    public void testTreePrintInteger() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        123,
                        "123"
                ),
                "TestLeaf \"123\" 123\n"
        );
    }

    @Test
    public void testTreePrintLong() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        234L,
                        "234"
                ),
                "TestLeaf \"234\" 234L\n"
        );
    }

    @Test
    public void testTreePrintString() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        "abc",
                        "\"abc\""
                ),
                "TestLeaf \"abc\" \"abc\"\n"
        );
    }

    @Test
    public void testTreePrintStringBuilder() {
        this.treePrintAndCheck(
                new TestLeafParserToken<>(
                        new StringBuilder("def"),
                        "\"def\""
                ),
                "TestLeaf \"def\" \"def\" (java.lang.StringBuilder)\n"
        );
    }

    static final class TestLeafParserToken<T> extends LeafParserToken<T> {

        TestLeafParserToken(final T value,
                            final String text) {
            super(
                    value,
                    text
            );
        }

        @Override
        public void accept(final ParserTokenVisitor visitor) {
            throw new UnsupportedOperationException();
        }

        @Override //
        boolean canBeEqual(final Object other) {
            throw new UnsupportedOperationException();
        }
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
