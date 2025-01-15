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

/**
 * Represents a result of a parser attempt to consume a {@link walkingkooka.text.cursor.TextCursor}
 */
abstract class ValueParserToken<V> implements ParserToken, Value<V> {

    /**
     * Package private ctor to limit subclassing.
     */
    ValueParserToken(final V value, final String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public final V value() {
        return this.value;
    }

    private final V value;

    /**
     * The text matched by the {@link Parser}.
     */
    @Override
    public final String text() {
        return this.text;
    }

    private final String text;

    @Override
    public final boolean isLeaf() {
        return false == this.isParent();
    }

    @Override
    public final boolean isParent() {
        return this instanceof RepeatedOrSequenceParserToken;
    }

    /**
     * Sub classes have a value, so cant be symbols.
     */
    @Override
    public final boolean isSymbol() {
        return false;
    }

    // Object

    @Override
    public final int hashCode() {
        return this.text.hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other || this.canBeEqual(other) && this.equals0((ValueParserToken<?>) other);
    }

    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final ValueParserToken<?> other) {
        return this.value.equals(other.value) && this.text.equals(other.text) && this.equals1(other);
    }

    abstract boolean equals1(final ValueParserToken<?> other);

    @Override
    public final String toString() {
        return this.text();
    }
}
