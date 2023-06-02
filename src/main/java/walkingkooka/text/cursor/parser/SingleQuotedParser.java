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

/**
 * A {@link Parser} that matches single quoted strings with support for backslash and unicode escape sequences.
 *
 * @param <C>
 */
final class SingleQuotedParser<C extends ParserContext> extends QuotedParser<C> {

    static <C extends ParserContext> SingleQuotedParser<C> instance() {
        return INSTANCE.cast();
    }

    private final static SingleQuotedParser<?> INSTANCE = new SingleQuotedParser<>("single quoted string");

    private SingleQuotedParser(final String toString) {
        super(toString);
    }

    @Override
    char quoteChar() {
        return '\'';
    }

    @Override
    SingleQuotedParserToken token(final String content, final String rawText) {
        return SingleQuotedParserToken.with(content, rawText);
    }

    // ParserSetToString..........................................................................................................

    @Override
    SingleQuotedParser<C> replaceToString(final String toString) {
        return new SingleQuotedParser<>(
                toString
        );
    }
}
