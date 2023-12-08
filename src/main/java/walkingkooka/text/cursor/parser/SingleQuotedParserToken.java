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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link ParserToken} with text surrounded by single quotes.
 */
public final class SingleQuotedParserToken extends QuotedParserToken {

    static SingleQuotedParserToken with(final String value, final String text) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(text, "text");

        if (!text.startsWith("'") || !text.endsWith("'")) {
            throw new IllegalArgumentException("text must start and end with '\'' but was " + text);
        }

        return new SingleQuotedParserToken(value, text);
    }

    private SingleQuotedParserToken(final String value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SingleQuotedParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SingleQuotedParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SingleQuotedParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SingleQuotedParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SingleQuotedParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                  final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SingleQuotedParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SingleQuotedParserToken replaceIf(final Predicate<ParserToken> predicate,
                                             final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SingleQuotedParserToken.class
        );
    }

    // visitor..........................................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SingleQuotedParserToken;
    }
}
