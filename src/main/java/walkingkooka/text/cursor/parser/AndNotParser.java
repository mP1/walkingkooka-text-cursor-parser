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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.Objects;
import java.util.Optional;

/**
 * A parser that implements a only returns a token if the first matches and the second fails.
 */
final class AndNotParser<C extends ParserContext> extends ParserSetToString<C>
    implements RequiredParser<C> {

    static <T extends ParserToken, C extends ParserContext> AndNotParser<C> with(final Parser<C> left, final Parser<C> right) {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");

        return new AndNotParser<>(
            left,
            right,
            left + " - " + right
        );
    }

    // @VisibleForTesting
    AndNotParser(final Parser<C> left,
                 final Parser<C> right,
                 final String toString) {
        super(toString);

        this.left = left;
        this.right = right;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor, final C context) {
        final TextCursorSavePoint save = cursor.save();

        Optional<ParserToken> leftResult = this.left.parse(cursor, context);
        if (leftResult.isPresent()) {

            final TextCursorSavePoint save2 = cursor.save();
            save.restore();

            final Optional<? extends ParserToken> rightResult = this.right.parse(cursor, context);
            if (rightResult.isPresent()) {
                leftResult = Optional.empty();
                save.restore();
            } else {
                save2.restore();
            }
        } else {
            save.restore();
        }

        return leftResult;
    }

    // ParserSetToString..........................................................................................................

    @Override
    AndNotParser<C> replaceToString(final String toString) {
        return new AndNotParser<>(
            this.left,
            this.right,
            toString
        );
    }

    private final Parser<C> left;
    private final Parser<C> right;

    // Object...........................................................................................................

    @Override //
    int hashCode0() {
        return Objects.hash(
            this.left,
            this.right
        );
    }

    @Override //
    boolean equalsParserSetToString(final ParserSetToString<?> other) {
        final AndNotParser<?> otherAndNotParser = other.cast();

        return this.left.equals(otherAndNotParser.left) &&
            this.right.equals(otherAndNotParser.right);
    }
}
