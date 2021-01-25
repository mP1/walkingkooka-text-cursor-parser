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

import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Used by {@link ParserTesting} to create an indented tree like value of {@link ParserToken tokens} in a tree.
 * <pre>
 * @Test
 * public void testDumpSequenceParserToken() {
 *     final List<ParserToken> tokens = Lists.of(ParserTokens.string("a1", "a1"),
 *             ParserTokens.bigDecimal(BigDecimal.valueOf(1.5), "1.5"),
 *             ParserTokens.bigInteger(BigInteger.valueOf(23), "23"));
 *
 *     this.dumpAndCheck(
 *             ParserTokens.sequence(tokens, ParserToken.text(tokens))
 *             , "Sequence\n" +
 *                     "  String=\"a1\" a1 (java.lang.String)\n" +
 *                     "  BigDecimal=\"1.5\" 1.5 (java.math.BigDecimal)\n" +
 *                     "  BigInteger=\"23\" 23 (java.math.BigInteger)\n");
 * }
 * </pre>
 */
final class ParserTestingPrettyDumper {

    static String dump(final ParserToken token) {
        return null != token ?
                dump0(token) :
                null;
    }

    private static String dump0(final ParserToken token) {
        final StringBuilder b = new StringBuilder();

        try (final IndentingPrinter printer = Printers.stringBuilder(b, LineEnding.NL).indenting(Indentation.with("  "))) {
            dump(token, printer);
            printer.flush();
        }

        return b.toString();
    }

    private static void dump(final ParserToken token,
                             final IndentingPrinter printer) {
        if (token instanceof LeafParserToken) {
            dumpLeaf((LeafParserToken) token, printer);
        }
        if (token instanceof ParentParserToken) {
            dumpParent((ParentParserToken) token, printer);
        }
    }

    private static void dumpLeaf(final LeafParserToken token,
                                 final IndentingPrinter printer) {
        final Object value = token.value();
        printer.print(
                typeName(token) + "=" +
                        CharSequences.quoteIfChars(token.text()) + " " + toString(value) +
                        (null != value ?
                                (" (" + value.getClass().getName() + ")") :
                                "")
        );
        printer.print(printer.lineEnding());
    }

    private static String toString(final Object value) {
        return value instanceof BigDecimal ?
                ((BigDecimal)value).toPlainString() : // want consistent printing of BigDecimals.
                String.valueOf(value);
    }

    private static void dumpParent(final ParentParserToken token,
                                   final IndentingPrinter printer) {
        printer.print(typeName(token));
        printer.print(printer.lineEnding());
        printer.indent();

        token.value().forEach(t -> dump(t, printer));
        printer.outdent();
    }

    private static String typeName(final ParserToken token) {
        final String typeName = token.getClass().getSimpleName();
        return typeName.endsWith(PARSER_TOKEN) ?
                typeName.substring(0, typeName.length() - PARSER_TOKEN.length()) :
                typeName;
    }

    private final static String PARSER_TOKEN = ParserToken.class.getSimpleName();

    private ParserTestingPrettyDumper() {
        throw new UnsupportedOperationException();
    }
}
