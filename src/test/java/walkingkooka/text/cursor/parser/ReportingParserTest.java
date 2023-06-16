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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.cursor.TextCursors;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReportingParserTest extends ParserWrapperTestCase<ReportingParser<ParserContext>> {

    private final static ParserReporterCondition CONDITION = ParserReporterCondition.ALWAYS;

    @Test
    public void testWithNullConditionFails() {
        assertThrows(
                NullPointerException.class,
                () -> ReportingParser.with(
                        null,
                        this.reporter(),
                        this.wrappedParser()
                )
        );
    }

    @Test
    public void testWithNullReporterFails() {
        assertThrows(
                NullPointerException.class,
                () -> ReportingParser.with(
                        CONDITION,
                        null,
                        this.wrappedParser()
                )
        );
    }

    @Test
    public void testReportFails() {
        this.parseThrows(
                "!",
                "Invalid character \'!\' at (1,1)"
        );
    }

    @Test
    public void testNotEmptyConditionCursorEmptyNotFails() {
        this.parseThrows(
                this.createParser(ParserReporterCondition.NOT_EMPTY),
                "!",
                "Invalid character \'!\' at (1,1)"
        );
    }

    @Test
    public void testNotEmptyConditionCursorNotEmptyParserSuccessful() {
        this.parseAndCheck(this.createParser(ParserReporterCondition.NOT_EMPTY),
                "A",
                ParserTokens.character('A', "A"),
                "A");
    }

    @Test
    public void testNotEmptyConditionCursorEmpty() {
        this.checkEquals(Optional.empty(), this.createParser(ParserReporterCondition.NOT_EMPTY).parse(TextCursors.charSequence(""), this.createContext()));
    }

    @Test
    @Override
    public void testEmptyCursorFail() {
        this.parseThrows(
                "!",
                "Invalid character \'!\' at (1,1)"
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createParser(),
                "letter | BasicParserReporter"
        );
    }

//    @Override
//    public ReportingParser<ParserContext> createParser() {
//        return this.createParser(ParserReporterCondition.ALWAYS);
//    }

    @Override
    Parser<ParserContext> wrappedParser() {
        return Parsers.character(CharPredicates.letter()).cast();
    }

    private ReportingParser<ParserContext> createParser(final ParserReporterCondition condition) {
        return ReportingParser.with(
                condition,
                this.reporter(),
                this.wrappedParser()
        );
    }

    ReportingParser<ParserContext> createParser(final Parser<ParserContext> parser) {
        return ReportingParser.with(
                ParserReporterCondition.ALWAYS,
                this.reporter(),
                parser
        );
    }

    private ParserReporter<ParserContext> reporter() {
        return ParserReporters.basic();
    }

    @Override
    public Class<ReportingParser<ParserContext>> type() {
        return Cast.to(ReportingParser.class);
    }
}
