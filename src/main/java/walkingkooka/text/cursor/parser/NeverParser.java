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

import walkingkooka.Cast;
import walkingkooka.text.cursor.TextCursor;

import java.util.Optional;

/**
 * A {@link Parser} that always returns {@link Optional#empty() nothing}
 */
final class NeverParser<C extends ParserContext> implements Parser<C> {

    static <C extends ParserContext> NeverParser<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NeverParser INSTANCE = new NeverParser();

    private NeverParser() {
        super();
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final C context) {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "";
    }
}
