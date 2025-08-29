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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;

/**
 * A common base class for both {@link RepeatedParserToken} and {@link SequenceParserToken}.
 */
abstract public class RepeatedOrSequenceParserToken extends ValueParserToken<List<ParserToken>>
    implements ParserToken,
    Value<List<ParserToken>> {

    /**
     * Private ctor to limit subclassing.
     */
    RepeatedOrSequenceParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Tokens must not be empty");
        }
    }

    /**
     * Sub-classes must create a public setValue and call this method and cast this.
     */
    final ValueParserToken<List<ParserToken>> setValue(final List<ParserToken> value) {
        Objects.requireNonNull(value, "values");

        final List<ParserToken> copy = Lists.immutable(value);
        return this.value().equals(copy) ?
            this :
            this.replaceValue(copy);
    }

    abstract ValueParserToken<List<ParserToken>> replaceValue(final List<ParserToken> value);

    /**
     * Recursively flattens all embedded {@link RepeatedOrSequenceParserToken} into a single {@link RepeatedOrSequenceParserToken}.
     */
    public abstract RepeatedOrSequenceParserToken flat();

// ParserTokenVisitor...............................................................................................

    final void acceptValues(final ParserTokenVisitor visitor) {
        for (ParserToken token : this.value()) {
            visitor.accept(token);
        }
    }
}
