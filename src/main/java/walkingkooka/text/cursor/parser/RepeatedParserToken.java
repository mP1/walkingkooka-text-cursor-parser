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

import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This {@link ParserToken} holds one or more of the tokens of the same type but not equal.
 */
public final class RepeatedParserToken extends RepeatedOrSequenceParserToken {

    static RepeatedParserToken with(final List<ParserToken> tokens, final String text) {
        Objects.requireNonNull(tokens, "tokens");
        Objects.requireNonNull(text, "text");

        return new RepeatedParserToken(tokens, text);
    }

    private RepeatedParserToken(final List<ParserToken> tokens, final String text) {
        super(tokens, text);
    }

    @Override RepeatedParserToken replaceValue(final List<ParserToken> value) {
        return new RepeatedParserToken(value, this.text());
    }

    @Override
    public RepeatedParserToken flat() {
        return this.setValue(RepeatedOrSequenceParserTokenFlatParserTokenVisitor.flat(this))
                .cast(RepeatedParserToken.class);
    }

    // children.........................................................................................................

    @Override
    public RepeatedParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                RepeatedParserToken::new
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public RepeatedParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveFirstIf(
                this,
                predicate,
                RepeatedParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public RepeatedParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                RepeatedParserToken.class
        );
    }

    // replaceFirstIf..................................................................................................

    @Override
    public RepeatedParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                              final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                RepeatedParserToken.class
        );
    }

    // ParserTokenVisitor...............................................................................................

    @Override
    public void accept(final ParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof RepeatedParserToken;
    }
}
