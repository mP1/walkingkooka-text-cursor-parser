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

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ParserWrapperTestCase<P extends ParserWrapper<ParserContext>> extends ParserTestCase<P> {

    ParserWrapperTestCase() {
        super();
    }

    @Test
    public final void testWrapNullParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createParser(null));
    }

    @Test
    public final void testMinCount() {
        this.minCountAndCheck(
            this.createParser(),
            1
        );
    }

    @Test
    public final void testMaxCount() {
        this.maxCountAndCheck(
            this.createParser(),
            1
        );
    }

    @Override
    public final P createParser() {
        return this.createParser(
            this.wrappedParser()
        );
    }

    abstract P createParser(final Parser<ParserContext> parser);

    abstract Parser<ParserContext> wrappedParser();
}
