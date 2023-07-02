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
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The parser token for a number with the value contained in a {@link Long}.
 */
public final class LongParserToken extends LeafParserToken<Long> {

    public static LongParserToken with(final long value, final String text) {
        Objects.requireNonNull(text, "text");

        return new LongParserToken(value, text);
    }

    private LongParserToken(final Long value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<LongParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                LongParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<LongParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                LongParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public LongParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                          final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                LongParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public LongParserToken replaceIf(final Predicate<ParserToken> predicate,
                                     final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                LongParserToken.class
        );
    }

    // visitor..........................................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof LongParserToken;
    }

    @Override
    boolean equals1(final ValueParserToken<?> other) {
        return true; // no extra properties to compare
    }
}
