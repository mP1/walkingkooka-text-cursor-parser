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

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The parser token for a zoned date+time with the value contained in a {@link ZonedDateTime}.
 */
public final class ZonedDateTimeParserToken extends LeafParserToken<ZonedDateTime> {

    public static ZonedDateTimeParserToken with(final ZonedDateTime value, final String text) {
        Objects.requireNonNull(text, "text");

        return new ZonedDateTimeParserToken(value, text);
    }

    private ZonedDateTimeParserToken(final ZonedDateTime value, final String text) {
        super(value, text);
    }

    // replaceFirstIf...................................................................................................

    @Override
    public ZonedDateTimeParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                   final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                ZonedDateTimeParserToken.class
        );
    }

    // visitor..........................................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof ZonedDateTimeParserToken;
    }

    @Override
    boolean equals1(final ValueParserToken<?> other) {
        return true; // no extra properties to compare
    }
}
