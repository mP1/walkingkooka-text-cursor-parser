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
import java.util.function.Consumer;
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
            for (final ParserToken child : (List<ParserToken>) ((Value<?>) this).value()) {
                child.collect(consumer);
            }
        }
    }

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        final Object value = ((Value<?>) this).value();

        final CharSequence quotedText = CharSequences.quoteAndEscape(this.text());

        if (this.isLeaf()) {
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
            final List<ParserToken> children = (List<ParserToken>) value;

            printer.println(ParserTokenTypeName.typeName(this) + " " + quotedText);

            printer.indent();

            for (final ParserToken child : children) {
                child.printTree(printer);
            }

            printer.outdent();
        }
    }
}
