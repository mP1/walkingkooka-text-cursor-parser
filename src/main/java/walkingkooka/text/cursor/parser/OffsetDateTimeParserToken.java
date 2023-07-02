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

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The parser token for a date+time with the value contained in a {@link OffsetDateTime}.
 */
public final class OffsetDateTimeParserToken extends LeafParserToken<OffsetDateTime> {

    public static OffsetDateTimeParserToken with(final OffsetDateTime value, final String text) {
        Objects.requireNonNull(text, "text");

        return new OffsetDateTimeParserToken(value, text);
    }

    private OffsetDateTimeParserToken(final OffsetDateTime value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<OffsetDateTimeParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                OffsetDateTimeParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public OffsetDateTimeParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                    final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                OffsetDateTimeParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public OffsetDateTimeParserToken replaceIf(final Predicate<ParserToken> predicate,
                                               final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                OffsetDateTimeParserToken.class
        );
    }

    // visitor..........................................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof OffsetDateTimeParserToken;
    }

    @Override
    boolean equals1(final ValueParserToken<?> other) {
        return true; // no extra properties to compare
    }
}
