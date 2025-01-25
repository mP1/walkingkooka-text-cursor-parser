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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.build.BuilderTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SequenceParserBuilderTest implements ClassTesting2<SequenceParserBuilder<ParserContext>>,
        BuilderTesting<SequenceParserBuilder<ParserContext>, Parser<ParserContext>> {

    private final static Parser<ParserContext> PARSER1 = parser("1");
    private final static Parser<ParserContext> PARSER2 = parser("2");
    private final static Parser<ParserContext> PARSER3 = parser("3");

    @Test
    public void testOptionalNullParserFails() {
        assertThrows(NullPointerException.class, () -> this.createBuilder().optional(null));
    }

    @Test
    public void testRequiredNullParserFails() {
        assertThrows(NullPointerException.class, () -> this.createBuilder().required(null));
    }

    @Test
    public void testOneOptionalParser() {
        this.createBuilder()
                .optional(PARSER1)
                .build();
    }

    @Test
    public void testOneRequiredParser() {
        this.createBuilder()
                .required(PARSER2)
                .build();
    }

    @Test
    public void testMoreThanTwoParsers() {
        this.createBuilder()
                .optional(PARSER1)
                .required(PARSER2)
                .build();
    }

    @Test
    public void testMoreThanTwoParsers2() {
        this.createBuilder()
                .optional(PARSER1)
                .required(PARSER2)
                .build();
    }

    @Test
    public void testParserBuilder() {
        PARSER1.builder()
                .optional(PARSER2.cast())
                .build();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createBuilder()
                        .optional(PARSER1)
                        .required(PARSER2)
                        .required(PARSER3),
                "[" + PARSER1 + "], " + PARSER2 + ", " + PARSER3
        );
    }

    @Override
    public SequenceParserBuilder<ParserContext> createBuilder() {
        return SequenceParserBuilder.empty();
    }

    private static Parser<ParserContext> parser(final String string) {
        return Parsers.string(string, CaseSensitivity.SENSITIVE);
    }

    @Override
    public Class<SequenceParserBuilder<ParserContext>> type() {
        return Cast.to(SequenceParserBuilder.class);
    }

    @Override
    public Class<Parser<ParserContext>> builderProductType() {
        return Cast.to(SequenceParserToken.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
