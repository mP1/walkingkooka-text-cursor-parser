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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Base for numerous parsers in this package.
 */
public abstract class ParserTestCase<P extends Parser<ParserContext>> implements ClassTesting2<P>,
        ParserTesting2<P, ParserContext>,
        ToStringTesting<P>,
        TypeNameTesting<P> {

    ParserTestCase() {
        super();
    }

    @Test
    public void testParseEmptyCursorFail() {
        this.parseFailAndCheck("");
    }

    @Test
    public final void testOrNullParserFails() {
        assertThrows(NullPointerException.class, () -> this.createParser().or(null));
    }

    @Test
    public final void testRepeating() {
        final Parser<ParserContext> parser = this.createParser().repeating();
        this.checkEquals(RepeatingParser.class, parser.getClass(), parser::toString);
    }

    @Test
    public void testOr() {
        final P parser = this.createParser();
        final P parser2 = this.createParser();
        this.checkEquals(Parsers.alternatives(Lists.of(parser.cast(), parser2.cast())), parser.or(parser2));
    }

    @Override
    public ParserContext createContext() {
        return ParserContexts.fake();
    }

    // setToString......................................................................................................

    @Test
    public final void testSetDifferentStringSameType() {
        final P parser = this.createParser();

        final String differentToString = "different123";
        final ParserSetToString<ParserContext> differentParser = Cast.to(
                parser.setToString(differentToString)
        );
        this.checkEquals(
                differentToString,
                differentParser.toString
        );

        this.checkEquals(
                differentToString,
                differentParser.toString()
        );
    }

    // TypeNameTesting .................................................................................................

    @Override
    public final String typeNamePrefix() {
        return "";
    }

    @Override
    public final String typeNameSuffix() {
        return Parser.class.getSimpleName();
    }

    // ClassTestCase ........................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
