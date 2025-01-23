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

public final class QuotedParserSingleTest extends QuotedParserTestCase<QuotedParserSingle<ParserContext>, SingleQuotedParserToken> {

    @Override
    public QuotedParserSingle<ParserContext> createParser() {
        return QuotedParserSingle.instance();
    }

    @Override
    char quoteChar() {
        return '\'';
    }

    @Override
    char otherQuoteChar() {
        return '"';
    }

    @Override
    SingleQuotedParserToken createToken(final String content,
                                        final String text) {
        return SingleQuotedParserToken.with(
                content,
                text
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringSingleQuoted() {
        this.toStringAndCheck(
                this.createParser(),
                "single quoted string"
        );
    }

    // class............................................................................................................

    @Override
    public Class<QuotedParserSingle<ParserContext>> type() {
        return Cast.to(QuotedParserSingle.class);
    }
}
