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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.BeanPropertiesTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.visit.Visiting;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A mixin interface with tests and helpers to assist testing of {@link ParserToken} implementations.
 *
 * @param <T>
 */
public interface ParserTokenTesting<T extends ParserToken > extends BeanPropertiesTesting,
        HashCodeEqualsDefinedTesting2<T>,
        HasTextTesting,
        TreePrintableTesting,
        ToStringTesting<T>,
        TypeNameTesting<T> {

    @Test
    default void testIsLeafDifferentIsParent() {
        final ParserToken token = this.createToken();

        final boolean leaf = token.isLeaf();
        final boolean parent = token.isParent();

        this.checkNotEquals(
                leaf,
                parent,
                () -> token + " isLeaf should be different from isParent"
        );
    }

    @Test
    default void testValueType() {
        for (; ; ) {
            final T token = this.createToken();
            if (token.isLeaf()) {
                final Object value = ((Value<?>) token).value();
                assertFalse(
                        value instanceof Collection,
                        () -> token + " value must not be a Collection but was " + value.getClass() + "=" + value
                );
                break;
            }
            if (token.isParent()) {
                final Object value = ((Value<?>) token).value();
                assertTrue(
                        value instanceof Collection,
                        () -> token + " value must be a Collection but was " + value.getClass() + "=" + value
                );
                break;
            }
            fail("ParserToken: " + token + " must implement either " + Value.class.getName());
        }
    }

    @Test
    default void testIsSymbol() {
        final String type = this.type().getSimpleName();
        final boolean symbol = type.contains(SYMBOL);

        final T token = this.createToken();

        if (type.contains(WHITESPACE)) {
            this.checkEquals(
                    true,
                    token.isSymbol(),
                    () -> "Token " + token + " is whitespace=true so isSymbol must also be true"
            );
        } else {
            this.checkEquals(
                    symbol,
                    token.isSymbol(),
                    () -> "Token " + token + " name includes " + SYMBOL + " so isSymbol should be true"
            );
        }
    }

    String SYMBOL = "Symbol";

    /**
     * If a type class name includes Whitespace its {@link ParserToken#isWhitespace()} should also return true.
     */
    @Test
    default void testIsWhitespace() {
        final String type = this.type().getSimpleName();
        final boolean whitespace = type.contains(WHITESPACE);

        final T token = this.createToken();
        this.checkEquals(
                whitespace,
                token.isWhitespace(),
                () -> token + " isWhitespace must be true if " + type + " contains " + CharSequences.quote(WHITESPACE)
        );

        if (whitespace) {
            this.checkEquals(
                    whitespace,
                    token.isNoise(),
                    () -> token + " isWhitespace==true, isNoise must also be true"
            );
            this.checkEquals(
                    whitespace,
                    token.isSymbol(),
                    () -> token + " isWhitespace==true, isSymbol must also be true"
            );
        }
    }

    String WHITESPACE = "Whitespace";

    @Test
    default void testWithNullTextFails() {
        assertThrows(NullPointerException.class, () -> this.createToken(null));
    }

    @Test
    default void testText() {
        this.textAndCheck(this.createToken(), this.text());
    }

    @Test
    default void testPublicStaticFactoryMethod() {
        final Class<?> type = this.type();
        final String suffix = ParserToken.class.getSimpleName();

        final String name = type.getSimpleName();
        final String without = Character.toLowerCase(name.charAt("".length())) +
                name.substring("".length() + 1, name.length() - suffix.length());

        String factoryMethodName1 = without;
        for (final String possible : new String[]{"boolean", "byte", "double", "equals", "int", "long", "null", "short"}) {
            if (without.equals(possible)) {
                factoryMethodName1 = without + suffix;
                break;
            }
        }
        final String factoryMethodName = factoryMethodName1;

        final List<Method> publicStaticMethods = Arrays.stream(ParserTokens.class.getMethods())
                .filter(m -> MethodAttributes.STATIC.is(m) && JavaVisibility.PUBLIC == JavaVisibility.of(m))
                .collect(Collectors.toList());

        final List<Method> factoryMethods = publicStaticMethods.stream()
                .filter(m -> m.getName().equals(factoryMethodName) &&
                        m.getReturnType().equals(type))
                .collect(Collectors.toList());

        final String publicStaticMethodsToString = publicStaticMethods.stream()
                .map(Method::toGenericString)
                .collect(Collectors.joining(LineEnding.SYSTEM.toString()));
        this.checkEquals(
                1,
                factoryMethods.size(),
                () -> "Expected only a single factory method called " + CharSequences.quote(factoryMethodName) +
                        " for " + type + " on " + ParserTokens.class.getName() + " but got " + factoryMethods + "\n" + publicStaticMethodsToString
        );
    }

    // children.........................................................................................................

    @Test
    default void testChildren() {
        final T token = this.createToken();
        if (token.isLeaf()) {
            this.checkEquals(
                    Lists.empty(),
                    token.children(),
                    token + " children"
            );
        } else {
            this.checkEquals(
                    ((Value<List<ParserToken>>) token).value(),
                    token.children(),
                    token + " children"
            );
        }
    }

    @Test
    default void testSetChildrenNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createToken().setChildren(null)
        );
    }

    @Test
    default void testSetChildrenWithSame() {
        final T token = this.createToken();

        assertSame(
                token,
                token.setChildren(token.children())
        );
    }

    @Test
    default void testSetChildrenWithSame2() {
        final T token = this.createToken();

        final List<ParserToken> children = Lists.array();
        children.addAll(token.children());

        assertSame(
                token,
                token.setChildren(children)
        );
    }

    @Test
    default void testSetChildrenNotEmptyIfLeafFails() {
        final T token = this.createToken();
        if (token.isLeaf()) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> token.setChildren(
                            Lists.of(
                                    ParserTokens.string(
                                            "Hello",
                                            "123"
                                    )
                            )
                    )
            );
        }
    }

    // removeFirstIf....................................................................................................

    @Test
    default void testParentRemoveFirstIfNullTokenFails() {
        assertThrows(
                NullPointerException.class,
                () -> ParserToken.parentRemoveFirstIf(
                        null,
                        Predicates.fake(),
                        ParserToken.class
                )
        );
    }

    @Test
    default void testParentRemoveFirstIfNullPredicateFails() {
        assertThrows(
                NullPointerException.class,
                () -> ParserToken.parentRemoveFirstIf(
                        this.createToken(),
                        null,
                        ParserToken.class
                )
        );
    }

    @Test
    default void testParentRemoveFirstIfNullTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> ParserToken.parentRemoveFirstIf(
                        this.createToken(),
                        Predicates.fake(),
                        null
                )
        );
    }

    @Test
    default void testRemoveFirstIfLeaf() {
        final T token = this.createToken();
        if (token.isLeaf()) {
            assertSame(
                    token,
                    token.removeFirstIf(Predicates.always())
            );
        }
    }

    @Test
    default void testRemoveFirstIfParentFirstChild() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 0) {
                final int index = 0;
                final ParserToken removed = children.get(index);

                final List<ParserToken> without = Lists.array();
                without.addAll(children);
                without.remove(index);

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeFirstIf(
                                    (t) -> t == removed
                            )
                    );
                }
            }
        }
    }

    @Test
    default void testRemoveFirstIfParentMiddleChild() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 3) {
                final int index = 1;
                final ParserToken removed = children.get(index);

                final List<ParserToken> without = Lists.array();
                without.addAll(children);
                without.remove(index);

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeFirstIf(
                                    (t) -> t == removed
                            )
                    );
                }
            }
        }
    }

    @Test
    default void testRemoveFirstIfParentLastChild() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 2) {
                final int index = children.size() - 1;
                final ParserToken removed = children.get(index);

                final List<ParserToken> without = Lists.array();
                without.addAll(children);
                without.remove(index);

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeFirstIf(
                                    (t) -> t == removed
                            )
                    );
                }
            }
        }
    }

    default void removeFirstIfAndCheck(final ParserToken token,
                                       final Predicate<ParserToken> predicate,
                                       final ParserToken expected) {
        this.checkEquals(
                expected,
                token.removeFirstIf(predicate),
                () -> token + " removeFirstIf " + predicate
        );
    }

    // removeIf....................................................................................................

    @Test
    default void testParentRemoveIfNullTokenFails() {
        assertThrows(
                NullPointerException.class,
                () -> ParserToken.parentRemoveIf(
                        null,
                        Predicates.fake(),
                        ParserToken.class
                )
        );
    }

    @Test
    default void testParentRemoveIfNullPredicateFails() {
        assertThrows(
                NullPointerException.class,
                () -> ParserToken.parentRemoveIf(
                        this.createToken(),
                        null,
                        ParserToken.class
                )
        );
    }

    @Test
    default void testParentRemoveIfNullTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> ParserToken.parentRemoveIf(
                        this.createToken(),
                        Predicates.fake(),
                        null
                )
        );
    }

    @Test
    default void testRemoveIfLeaf() {
        final T token = this.createToken();
        if (token.isLeaf()) {
            assertSame(
                    token,
                    token.removeIf(Predicates.always())
            );
        }
    }

    @Test
    default void testRemoveIfParentFirstChild() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 0) {
                final int index = 0;
                final ParserToken removed = children.get(index);

                final List<ParserToken> without = Lists.array();
                without.addAll(children);
                without.remove(index);

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeIf(
                                    (t) -> t == removed
                            )
                    );
                }
            }
        }
    }

    @Test
    default void testRemoveIfParentMiddleChild() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 3) {
                final int index = 1;
                final ParserToken removed = children.get(index);

                final List<ParserToken> without = Lists.array();
                without.addAll(children);
                without.remove(index);

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeIf(
                                    (t) -> t == removed
                            )
                    );
                }
            }
        }
    }

    @Test
    default void testRemoveIfParentLastChild() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 2) {
                final int index = children.size() - 1;
                final ParserToken removed = children.get(index);

                final List<ParserToken> without = Lists.array();
                without.addAll(children);
                without.remove(index);

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeIf(
                                    (t) -> t == removed
                            )
                    );
                }
            }
        }
    }

    @Test
    default void testRemoveIfParentAllChildren() {
        final T token = this.createToken();
        if (token.isParent()) {
            final List<ParserToken> children = token.children();
            if (children.size() > 2) {
                final List<ParserToken> without = Lists.empty();

                boolean skip;
                try {
                    token.setChildren(without);
                    skip = false;
                } catch (final Exception cantBeEmpty) {
                    skip = true;
                }
                ;

                if (false == skip) {
                    this.checkEquals(
                            token.setChildren(without),
                            token.removeIf(
                                    Predicates.always()
                            )
                    );
                }
            }
        }
    }

    default void removeIfAndCheck(final ParserToken token,
                                  final Predicate<ParserToken> predicate,
                                  final ParserToken expected) {
        this.checkEquals(
                expected,
                token.removeIf(predicate),
                () -> token + " removeIf " + predicate
        );
    }

    // Visitor..........................................................................................................

    @Test
    default void testAcceptStartParserTokenSkip() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final T token = this.createToken();

        new FakeParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("1");
                visited.add(t);
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("2");
                visited.add(t);
            }
        }.accept(token);
        this.checkEquals("12", b.toString());
        this.checkEquals(Lists.<Object>of(token, token), visited, "visited tokens");
    }

    @Test
    default void testIsNoisyGuess() {
        final T token = this.createToken();
        final String className = token.getClass().getSimpleName();
        this.checkEquals(
                className.contains("Whitespace") | className.contains("Symbol") | className.contains("Comment"),
                token.isNoise()
        );
    }

    @Test
    default void testPropertiesNeverReturnNull() throws Exception {
        this.allPropertiesNeverReturnNullCheck(this.createToken(), Predicates.never());
    }

    @Test
    default void testToString() {
        this.toStringAndCheck(this.createToken(), this.text());
    }

    default T createToken() {
        return this.createToken(this.text());
    }

    String text();

    T createToken(final String text);

    T createDifferentToken();

    // HashCodeEqualityTesting..........................................................................................

    @Override
    default T createObject() {
        return this.createToken();
    }

    // TypeNameTesting .................................................................................................

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return ParserToken.class.getSimpleName();
    }
}
