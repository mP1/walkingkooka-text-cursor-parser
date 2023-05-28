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
import walkingkooka.text.printer.IndentingPrinter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The parser token for a number with the value contained within a {@link BigDecimal}
 */
public final class BigDecimalParserToken extends ValueParserToken<BigDecimal> implements ParserToken {

    public static BigDecimalParserToken with(final BigDecimal value, final String text) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(text, "text");

        return new BigDecimalParserToken(value, text);
    }

    private BigDecimalParserToken(final BigDecimal value, final String text) {
        super(value, text);
    }

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Force consistent printing of {@link BigDecimal} values.
     */
    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.print(ParserTokenTypeName.typeName(this) + " " + CharSequences.quoteIfChars(this.text()) + " " + this.value().toPlainString() + " (java.math.BigDecimal)");
        printer.println();
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof BigDecimalParserToken;
    }

    @Override
    boolean equals1(final ValueParserToken<?> other) {
        return true; // no extra properties to compare
    }
}
