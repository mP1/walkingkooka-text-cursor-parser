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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link Parser} that delegates to another parser, and runs the given {@link BiFunction} on successful
 * tokens.
 */
final class TransformingParser<C extends ParserContext> extends ParserWrapper<C>
    implements TreePrintable {

    static <C extends ParserContext> TransformingParser<C> with(final Parser<C> parser,
                                                                final BiFunction<ParserToken, C, ParserToken> transformer) {
        Objects.requireNonNull((Parser<?>) parser, "parser");
        Objects.requireNonNull(transformer, "transformer");

        return new TransformingParser<>(
            parser,
            transformer,
            parser.toString()
        );
    }

    private TransformingParser(final Parser<C> parser,
                               final BiFunction<ParserToken, C, ParserToken> transformer,
                               final String toString) {
        super(parser, toString);
        this.transformer = transformer;
    }

    // Parser...........................................................................................................

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor, final C context) {
        final Optional<ParserToken> token = this.parser.parse(cursor, context);
        return token.map(parserToken -> this.transformer.apply(parserToken, context));

    }

    /**
     * A {@link BiFunction} that transforms successful tokens into another.
     */
    private final BiFunction<ParserToken, C, ParserToken> transformer;

    // ParserSetToString................................................................................................

    @Override
    TransformingParser<C> replaceToString(final String toString) {
        return new TransformingParser<>(
            this.parser,
            this.transformer,
            toString
        );
    }

    // Object...........................................................................................................

    @Override //
    int hashCode1() {
        return Objects.hash(
            this.transformer
        );
    }

    @Override //
    boolean equalsParserWrapper(final ParserWrapper<?> other) {
        final TransformingParser<?> otherTransformerParser = other.cast();

        return this.transformer.equals(otherTransformerParser.transformer);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            printer.println("parser");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.parser,
                    printer
                );
            }
            printer.outdent();

            printer.println("transformer");
            printer.indent();
            {
                final BiFunction<ParserToken, C, ParserToken> transformer = this.transformer;
                if (transformer instanceof TreePrintable) {
                    ((TreePrintable) transformer)
                        .printTree(printer);
                } else {
                    final String className = transformer.getClass().getName();
                    if (-1 == className.indexOf("$Lambda$")) {
                        TreePrintable.printTreeOrToString(
                            this.transformer,
                            printer
                        );
                    } else {
                        printer.println("Lambda");
                    }
                }
            }
            printer.outdent();
        }
        printer.outdent();
    }
}
