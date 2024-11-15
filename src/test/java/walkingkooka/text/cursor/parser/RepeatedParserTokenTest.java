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
import walkingkooka.collect.list.Lists;
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class RepeatedParserTokenTest extends RepeatedOrSequenceParserTokenTestCase<RepeatedParserToken> {

    // visitor..........................................................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final StringParserToken string = this.string("abc");
        final RepeatedParserToken token = this.createToken(string);

        new FakeParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("1");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("2");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final RepeatedParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final RepeatedParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final StringParserToken t) {
                b.append("5");
                visited.add(t);
            }
        }.accept(token);
        this.checkEquals("1315242", b.toString());
        this.checkEquals(Lists.<Object>of(token, token, string, string, string, token, token), visited, "visited tokens");
    }

    @Test
    public void testAcceptSkip() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final StringParserToken string = this.string("abc");
        final RepeatedParserToken token = this.createToken(string);

        new FakeParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("1");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("2");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final RepeatedParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final RepeatedParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }
        }.accept(token);
        this.checkEquals("1342", b.toString());
        this.checkEquals(Lists.of(token, token, token, token), visited, "visited tokens");
    }

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
                this.createToken(),
                "Repeated \"abc\"\n" +
                        "  String \"abc\" \"abc\"\n"
        );
    }

    @Override
    public RepeatedParserToken createToken(final String text) {
        return createToken(text, string(text));
    }

    @Override
    public String text() {
        return "abc";
    }

    @Override
    public RepeatedParserToken createDifferentToken() {
        return createToken("different", string("different"));
    }

    @Override
    RepeatedParserToken createToken(final List<ParserToken> value, final String text) {
        return RepeatedParserToken.with(value, text);
    }

    @Override
    public Class<RepeatedParserToken> type() {
        return RepeatedParserToken.class;
    }
}
