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

import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

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
 *                     "  String \"a1\" \"a1\" (java.lang.String)\n" +
 *                     "  BigDecimal \"1.5\" 1.5 (java.math.BigDecimal)\n" +
 *                     "  BigInteger \"23\" 23 (java.math.BigInteger)\n");
 * }
 * </pre>
 */
final class ParserTestingPrettyDumper {

    private final static Indentation INDENTATION = Indentation.with("  ");
    private final static LineEnding ENDING = LineEnding.NL;

    static String dump(final ParserToken token) {
        return null != token ?
                token.treeToString(INDENTATION, ENDING) :
                null;
    }

    private ParserTestingPrettyDumper() {
        throw new UnsupportedOperationException();
    }
}
