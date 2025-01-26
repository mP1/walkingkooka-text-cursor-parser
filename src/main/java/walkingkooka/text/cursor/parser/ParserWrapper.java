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

import java.util.Objects;

abstract class ParserWrapper<C extends ParserContext> extends ParserSetToString<C> {

    static void checkParser(final Parser<?> parser) {
        Objects.requireNonNull(parser, "parser");
    }

    ParserWrapper(final Parser<C> parser,
                  final String toString) {
        super(toString);
        this.parser = parser;
    }

    @Override
    public final int minCount() {
        return this.parser.minCount();
    }

    @Override
    public final int maxCount() {
        return this.parser.maxCount();
    }

    final Parser<C> parser;

    // Object...........................................................................................................

    @Override //
    final int hashCode0() {
        return Objects.hash(
                this.parser,
                this.hashCode1()
        );
    }

    abstract int hashCode1();

    @Override //
    final boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final ParserWrapper<?> otherParserWrapper = (ParserWrapper<?>) other;

        return this.parser.equals(otherParserWrapper.parser) &&
                this.equalsParserWrapper(otherParserWrapper);
    }

    abstract boolean equalsParserWrapper(final ParserWrapper<?> other);
}
