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

import walkingkooka.Context;
import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.text.cursor.TextCursor;

/**
 * A {@link Context} that accompanies a parser invocation. This may be used to hold state that is used during parsing.
 */
public interface ParserContext extends DateTimeContext,
    DecimalNumberContext {

    /**
     * Factory that creates a {@link InvalidCharacterException} for the current {@link TextCursor#at()}.
     * The context is able to custom whether the message will include the position or text offset or column and line.
     */
    InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                        final TextCursor cursor);
}
