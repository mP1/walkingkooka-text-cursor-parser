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
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;

/**
 * Interface that all leaf parser tokens must implement. A leaf must not have further children or a List value.
 */
public interface LeafParserToken<T> extends ParserToken, Value<T> {

    // TreePrintable....................................................................................................

    @Override
    default void printTree(final IndentingPrinter printer) {
        final T value = this.value();

        printer.print(
                ParserTokenTypeName.typeName(this) +
                        " " +
                        CharSequences.quoteAndEscape(this.text()) +
                        " " +
                        (null == value ?
                                null :
                                CharSequences.quoteIfChars(value) +
                                        " (" +
                                        value.getClass().getName() +
                                        ")"
                        )
        );
        printer.println();
    }
}
