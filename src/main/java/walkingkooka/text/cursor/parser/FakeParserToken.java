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

import walkingkooka.test.Fake;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.List;
import java.util.function.Predicate;

public class FakeParserToken implements ParserToken, Fake {
    @Override
    public String text() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLeaf() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParserToken setChildren(final List<ParserToken> children) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParserToken removeIf(final Predicate<ParserToken> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printTree(IndentingPrinter indentingPrinter) {
        throw new UnsupportedOperationException();
    }
}
