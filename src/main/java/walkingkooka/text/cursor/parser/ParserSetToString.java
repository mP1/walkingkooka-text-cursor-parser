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
 * A {@link Parser} that includes a {@link #toString} property and adds a guard within {@link #setToString(String)}
 * only calling a package private {@link #replaceToString(String)} if the new string is different.
 */
abstract class ParserSetToString<C extends ParserContext> implements Parser<C> {

    ParserSetToString(final String toString) {
        this.toString = toString;
    }

    @Override
    public final Parser<C> setToString(final String toString) {
        Objects.requireNonNull(toString, "toString");

        return this.toString.equals(toString) ?
                this :
                this.replaceToString(toString);
    }

    /**
     * Requests the sub-class to create a new instance passing the given {@link String} to the ctor which will
     * become the new {@link #toString}.
     */
    abstract Parser<C> replaceToString(final String toString);

    // Object..........................................................................................................

    @Override
    public final String toString() {
        return this.toString;
    }

    /**
     * The actual returned {@link #toString}.
     */
    final String toString;
}
