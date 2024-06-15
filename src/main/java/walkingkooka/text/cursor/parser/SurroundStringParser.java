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
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Parser} that only requires an opening string and is terminated by another closing string.
 */
final class SurroundStringParser<C extends ParserContext> extends NonEmptyParser<C> {

    static <C extends ParserContext> SurroundStringParser<C> with(final String open, final String close) {
        return new SurroundStringParser<>(
                CharSequences.failIfNullOrEmpty(open, "open"),
                CharSequences.failIfNullOrEmpty(close, "close"),
                CharSequences.quoteAndEscape(open) +
                        "*" +
                        CharSequences.quoteAndEscape(close)
        );
    }

    private SurroundStringParser(final String open,
                                 final String close,
                                 final String toString) {
        super(toString);

        this.open = open;
        this.close = close;
    }

    @Override
    Optional<ParserToken> tryParse(final TextCursor cursor,
                                   final C context,
                                   final TextCursorSavePoint start) {
        StringParserToken result = null;

        int matched = 0;
        final String open = this.open;

        for (; ; ) {
            if (cursor.isEmpty() || open.charAt(matched) != cursor.at()) {
                break;
            }
            matched++;
            cursor.next();

            if (open.length() == matched) {
                final String close = this.close;
                matched = 0;

                // try matching close...
                for (; ; ) {
                    if (cursor.isEmpty()) {
                        break;
                    }
                    final char at = cursor.at();
                    if (close.charAt(matched) == at) {
                        matched++;
                        cursor.next();

                        if (close.length() == matched) {
                            // close found, match!!!
                            final String text = start.textBetween().toString();
                            result = StringParserToken.with(text, text);
                            break;
                        }
                        continue;
                    }
                    matched = 0;
                    if (close.charAt(0) == at) {
                        matched++;

                        if (close.length() == matched) {
                            // close found, match!!!
                            final String text = start.textBetween().toString();
                            result = StringParserToken.with(text, text);
                            break;
                        }
                    }

                    cursor.next();
                }
                // end
                break;
            }
        }

        return Optional.ofNullable(result);
    }

    private final String open;

    private final String close;

    // ParserSetToString..........................................................................................................

    @Override
    SurroundStringParser<C> replaceToString(final String toString) {
        return new SurroundStringParser<>(
                this.open,
                this.close,
                toString
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.open,
                this.close,
                this.toString
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SurroundStringParser && this.equals0((SurroundStringParser<?>) other);
    }

    private boolean equals0(final SurroundStringParser<?> other) {
        return this.open.equals(other.open) &&
                this.close.equals(other.close) &&
                this.toString.equals(other.toString);
    }
}
