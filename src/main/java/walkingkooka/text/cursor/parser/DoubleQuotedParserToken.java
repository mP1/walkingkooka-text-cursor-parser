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

/**
 * A {@link ParserToken} with text surrounded by double quotes.
 */
public final class DoubleQuotedParserToken extends QuotedParserToken {

    static DoubleQuotedParserToken with(final String value, final String text) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(text, "text");

        if (!text.startsWith("\"") || !text.endsWith("\"")) {
            throw new IllegalArgumentException("text must start and end with '\"' but was " + text);
        }

        return new DoubleQuotedParserToken(value, text);
    }

    private DoubleQuotedParserToken(final String value, final String text) {
        super(value, text);
    }

    // visitor..........................................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

}
