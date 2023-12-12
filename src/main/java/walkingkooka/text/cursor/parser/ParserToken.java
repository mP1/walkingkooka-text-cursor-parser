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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a result of a parser attempt to consume a {@link walkingkooka.text.cursor.TextCursor}
 */
public interface ParserToken extends HasText,
        TreePrintable {

    /**
     * A empty list, useful for parent {@link ParserToken} without children.
     */
    List<ParserToken> EMPTY = Lists.empty();

    /**
     * Returns a {@link List} without any {@link ParserToken tokens} that return true for {@link #isNoise()}.
     */
    static List<ParserToken> filterWithoutNoise(final List<ParserToken> value) {
        return value.stream()
                .filter(t -> !t.isNoise())
                .collect(Collectors.toList());
    }

    /**
     * Concatenates the text from all given tokens into a single string.
     */
    static String text(final List<? extends ParserToken> tokens) {
        Objects.requireNonNull(tokens, "tokens");

        return tokens.stream()
                .map(HasText::text)
                .collect(Collectors.joining());
    }

    /**
     * Returns true for leaf {@link ParserToken}. A leaf must implement {@link Value}
     */
    boolean isLeaf();

    /**
     * Returns true for parent {@link ParserToken} which will also implement {@link Value} which returns a {@link List} of child {@link ParserToken}
     */
    boolean isParent();

    /**
     * Only returns true for noise tokens like whitespace.
     */
    default boolean isNoise() {
        return false;
    }

    /**
     * Only symbols should return true. A whitespace is also a symbol.
     */
    boolean isSymbol();

    /**
     * Only returns true for whitespace tokens but not other types of token..
     */
    default boolean isWhitespace() {
        return false;
    }

    /**
     * Returns a {@link List} view of any children. Leaf {@link ParserToken} those that return true for {@link #isLeaf()} will return an empty {@link List}.
     * Note this is not recursively it only returns the immediate children and NOT descendants.
     */
    default List<ParserToken> children() {
        return this.isLeaf() ?
                Lists.empty() :
                ((Value<List<ParserToken>>) this).value();
    }

    /**
     * Setter which returns a new {@link ParserToken} with the given children. Leaf tokens will fail if the new children
     * is not empty.
     */
    ParserToken setChildren(final List<ParserToken> children);

    /**
     * Helper that should be called by all {@link ParserToken#isLeaf()} that return true.
     */
    static <T extends ParserToken> T leafSetChildren(final T token,
                                                     final List<ParserToken> children) {
        checkToken(token);
        Objects.requireNonNull(children, "children");

        if (false == children.isEmpty()) {
            throw new IllegalArgumentException(
                    "Expected zero children for leaf parser token but got " +
                            children.size() +
                            " children=" +
                            children
            );
        }

        return token;
    }

    /**
     * Helper that should be called by all parent {@link ParserToken#setChildren(List)}.
     * It first takes a defensive copy of the given children, tests if these are different from the current,
     * and returns this or the factory as necessary.
     */
    static <T extends ParserToken> T parentSetChildren(final T token,
                                                       final List<ParserToken> children,
                                                       final BiFunction<List<ParserToken>, String, T> factory) {
        checkToken(token);
        Objects.requireNonNull(children, "children");
        final List<ParserToken> copy = Lists.immutable(children);
        Objects.requireNonNull(factory, "factory");

        return token.children().equals(copy) ?
                token :
                factory.apply(
                        copy,
                        ParserToken.text(copy)
                );
    }

    // findFirst........................................................................................................

    /**
     * Walks the graph starting at this {@link ParserToken} until the {@link Predicate} returns true.
     */
    default Optional<ParserToken> findFirst(final Predicate<ParserToken> predicate) {
        checkPredicate(predicate);

        Optional<ParserToken> result = Optional.empty();

        if (predicate.test(this)) {
            result = Optional.of(this);
        } else {
            for (final ParserToken child : this.children()) {
                result = child.findFirst(predicate);
                if (result.isPresent()) {
                    break;
                }
            }
        }

        return result;
    }

    // findFirst........................................................................................................

    /**
     * Walks the graph starting at this {@link ParserToken} passing all tokens that match the given {@link Predicate}
     * to the provided {@link Consumer}.
     */
    default void findIf(final Predicate<ParserToken> predicate,
                        final Consumer<ParserToken> consumer) {
        checkPredicate(predicate);
        Objects.requireNonNull(consumer, "consumer");

        if (predicate.test(this)) {
            consumer.accept(this);
        }

        for (final ParserToken child : this.children()) {
            child.findIf(predicate, consumer);
        }
    }

    // removeFirstIf....................................................................................................

    /**
     * Removes the first {@link ParserToken} that is matched by the {@link Predicate}. Leaf tokens will always return
     * this, while parents will search all descendants starting with their children. If a parent requires at least one child
     * and that child is removed then any thrown {@link Throwable} will still happen.
     */
    default Optional<ParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return this.isLeaf() ?
                ParserTokens.removeFirstIfLeaf(
                        this,
                        predicate
                ) :
                ParserTokens.removeFirstIfParent(
                        this,
                        predicate
                );
    }

    // removeIf.........................................................................................................

    /**
     * Removes all {@link ParserToken} that is matched by the {@link Predicate}. Leaf tokens will always return
     * this, while parents will search all descendants starting with their children. If a parent requires at least one child
     * and that child is removed then any thrown {@link Throwable} will still happen.
     */
    default Optional<ParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return this.isLeaf() ?
                ParserTokens.removeIfLeaf(
                        this,
                        predicate
                ) :
                ParserTokens.removeIfParent(
                        this,
                        predicate
                );
    }

    // replaceFirstIf....................................................................................................

    /**
     * Walks the token graph starting at this token, using the {@link Predicate} to find a match using the {@link Function}
     * to supply the replacement.
     */
    ParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                               final Function<ParserToken, ParserToken> mapper);

    /**
     * Helper invoked by {@link ParserToken#replaceFirstIf(Predicate, Function)}.
     */
    static <T extends ParserToken> T replaceFirstIf(final ParserToken token,
                                                    final Predicate<ParserToken> predicate,
                                                    final Function<ParserToken, ParserToken> mapper,
                                                    final Class<T> type) {
        checkToken(token);
        checkPredicate(predicate);
        Objects.requireNonNull(mapper, "mapper");
        checkType(type);

        final boolean[] stop = new boolean[1];
        return ParserTokens.replaceFirstIf(
                token,
                predicate,
                mapper,
                stop
        ).cast(type);
    }

    // replaceIf....................................................................................................

    /**
     * Walks the token graph starting at this token, using the {@link Predicate} to find a match and when found replaces
     * the match with the given {@link ParserToken}. Unlike {@link #removeFirstIf(Predicate)} this works on leaf tokens.
     */
    ParserToken replaceIf(final Predicate<ParserToken> predicate,
                          final Function<ParserToken, ParserToken> mapper);

    /**
     * Helper invoked by {@link ParserToken#replaceIf(Predicate, Function)}.
     */
    static <T extends ParserToken> T replaceIf(final ParserToken token,
                                               final Predicate<ParserToken> predicate,
                                               final Function<ParserToken, ParserToken> mapper,
                                               final Class<T> type) {
        checkToken(token);
        checkPredicate(predicate);
        Objects.requireNonNull(mapper, "replacement");
        checkType(type);

        return ParserTokens.replaceIf(
                token,
                predicate,
                mapper
        ).cast(type);
    }

    private static <T extends ParserToken> T checkParent(final T parent) {
        return Objects.requireNonNull(parent, "parent");
    }

    private static Predicate<ParserToken> checkPredicate(final Predicate<ParserToken> predicate) {
        return Objects.requireNonNull(predicate, "predicate");
    }

    private static ParserToken checkToken(final ParserToken token) {
        return Objects.requireNonNull(token, "token");
    }

    private static <T extends ParserToken> Class<T> checkType(final Class<T> type) {
        return Objects.requireNonNull(type, "type");
    }

    // ParserTokenVisitor...............................................................................................

    /**
     * Called by the visitor responsible for this group of tokens, which typically resides in the same package.
     * The token must then call the appropriate visit or start/end visit and also visit any child token values as appropriate.
     */
    void accept(final ParserTokenVisitor visitor);

    /**
     * Useful to get help reduce casting noise.
     * Note the type parameter {@link Class#cast} method is not invoked to keep this compatible with j2cl.
     */
    default <T extends ParserToken> T cast(final Class<T> type) {
        return (T) this;
    }

    /**
     * Recursively visits all {@link ParserToken} in this graph, passing each one to the {@link Consumer} for filtering / collecting
     * aka usage.
     */
    default void collect(final Consumer<ParserToken> consumer) {
        Objects.requireNonNull(consumer, "consumer");

        consumer.accept(this);


        if (this.isParent()) {
            for (final ParserToken child : this.children()) {
                child.collect(consumer);
            }
        }
    }

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        final CharSequence quotedText = CharSequences.quoteAndEscape(this.text());

        if (this.isLeaf()) {
            final Object value = ((Value<?>) this).value();

            printer.println(
                    ParserTokenTypeName.typeName(this) +
                            " " +
                            quotedText +
                            " " +
                            (null == value ?
                                    null :
                                    CharSequences.quoteIfChars(value) +
                                            " (" +
                                            value.getClass().getName() +
                                            ")"
                            )
            );
        }
        if (this.isParent()) {
            final List<ParserToken> children = this.children();

            printer.println(
                    ParserTokenTypeName.typeName(this) +
                            " " +
                            quotedText
            );

            printer.indent();

            for (final ParserToken child : children) {
                child.printTree(printer);
            }

            printer.outdent();
        }
    }
}
