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
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.fail;

public interface ParserReporterTesting extends TreePrintableTesting {

    default <C extends ParserContext> void reportAndCheck(final ParserReporter<C> reporter,
                                                          final TextCursor cursor,
                                                          final C context,
                                                          final Parser<C> parser,
                                                          final String messageContains) {
        this.checkEquals(
            false,
            CharSequences.isNullOrEmpty(messageContains),
            "messageContains must not be null or empty"
        );

        final TextCursorSavePoint save = cursor.save();
        try {
            this.report(
                reporter,
                cursor,
                context,
                parser
            );
            fail("Reporter should have reported exception");
        } catch (final RuntimeException expected) {
            save.restore();
            final String message = expected.getMessage();
            this.checkEquals(
                true,
                message.contains(messageContains),
                () -> "report message: " + CharSequences.quoteAndEscape(message) + " missing contains: " + CharSequences.quoteAndEscape(messageContains)
            );
        }
    }


    default <C extends ParserContext> void report(final ParserReporter<C> reporter,
                                                  final TextCursor cursor,
                                                  final C context,
                                                  final Parser<C> parser) {
        reporter.report(
            cursor,
            context,
            parser
        );
    }
}
