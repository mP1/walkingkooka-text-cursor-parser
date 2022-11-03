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

import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Parser} that only matches the given {@link String} which must not be null or empty.
 */
final class StringParser<C extends ParserContext> extends NonEmptyParser<C> {

    static <C extends ParserContext> StringParser<C> with(final String string, final CaseSensitivity caseSensitivity) {
        CharSequences.failIfNullOrEmpty(string, "string");
        Objects.requireNonNull(caseSensitivity, "caseSensitivity");

        final StringBuilder b = new StringBuilder();

        b.append(CharSequences.quoteAndEscape(string));
        if (CaseSensitivity.INSENSITIVE == caseSensitivity) {
            b.append(" (CaseInsensitive)");
        }

        return new StringParser<>(
                string,
                caseSensitivity,
                b.toString()
        );
    }

    private StringParser(final String string,
                         final CaseSensitivity caseSensitivity,
                         final String toString) {
        super(toString);

        this.string = string;
        this.caseSensitivity = caseSensitivity;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint start) {
        final String string = this.string;
        final CaseSensitivity caseSensitivity = this.caseSensitivity;

        StringParserToken result = null;
        int matched = 0;

        for (; ; ) {
            if (cursor.isEmpty() || false == caseSensitivity.isEqual(string.charAt(matched), cursor.at())) {
                break;
            }
            matched++;
            cursor.next();

            if (string.length() == matched) {
                final String text = start.textBetween().toString();
                result = StringParserToken.with(text, text);
                break;
            }
        }

        return Optional.ofNullable(result);
    }

    private final String string;
    private final CaseSensitivity caseSensitivity;

    // Object.................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.string, this.caseSensitivity);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof StringParser && this.equals0((StringParser<?>) other);
    }

    private boolean equals0(final StringParser<?> other) {
        return this.string.equals(other.string) &&
                this.caseSensitivity.equals(other.caseSensitivity);
    }

    // Parser2..........................................................................................................

    @Override
    StringParser<C> replaceToString(final String toString) {
        return new StringParser<>(
                this.string,
                this.caseSensitivity,
                toString
        );
    }
}
