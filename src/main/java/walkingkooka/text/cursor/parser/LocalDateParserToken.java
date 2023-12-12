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

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The parser token for a date with the value contained in a {@link LocalDate}.
 */
public final class LocalDateParserToken extends LeafParserToken<LocalDate> {

    public static LocalDateParserToken with(final LocalDate value, final String text) {
        Objects.requireNonNull(text, "text");

        return new LocalDateParserToken(value, text);
    }

    private LocalDateParserToken(final LocalDate value, final String text) {
        super(value, text);
    }

    // replaceFirstIf...................................................................................................

    @Override
    public LocalDateParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                               final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                LocalDateParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public LocalDateParserToken replaceIf(final Predicate<ParserToken> predicate,
                                          final Function<ParserToken, ParserToken> token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                LocalDateParserToken.class
        );
    }

    // visitor..........................................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof LocalDateParserToken;
    }

    @Override
    boolean equals1(final ValueParserToken<?> other) {
        return true; // no extra properties to compare
    }
}
