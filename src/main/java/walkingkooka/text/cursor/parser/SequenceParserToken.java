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

import walkingkooka.collect.list.Lists;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;

/**
 * This {@link ParserToken} holds a sequence in order of tokens.
 */
public final class SequenceParserToken extends RepeatedOrSequenceParserToken {

    /**
     * Factory that wraps many tokens in a {@link SequenceParserToken}.
     */
    static SequenceParserToken with(final List<ParserToken> tokens, final String text) {
        Objects.requireNonNull(tokens, "tokens");
        Objects.requireNonNull(text, "text");

        return new SequenceParserToken(tokens, text);
    }

    private SequenceParserToken(final List<ParserToken> tokens, final String text) {
        super(tokens, text);
    }

    @Override //
    SequenceParserToken replaceValue(final List<ParserToken> value) {
        return new SequenceParserToken(value, this.text());
    }

    @Override
    public SequenceParserToken flat() {
        return this.setValue(RepeatedOrSequenceParserTokenFlatParserTokenVisitor.flat(this))
                .cast(SequenceParserToken.class);
    }

    public <T extends ParserToken> T required(final int index, final Class<T> type) {
        final List<ParserToken> tokens = this.value();
        try {
            return tokens.get(index).cast(type);
        } catch (final IndexOutOfBoundsException cause) {
            throw new IndexOutOfBoundsException("Invalid index " + index + " must be between 0 and " + tokens.size());
        }
    }

    // BinaryOperatorTransformer......................................................................................

    /**
     * Takes this {@link SequenceParserToken} and possibly rearranges tokens creating binary operators and unary negative tokens
     * as necessary honouring the priorities replied by the given {@link BinaryOperatorTransformer}.
     */
    public ParserToken binaryOperator(final BinaryOperatorTransformer transformer) {
        Objects.requireNonNull(transformer, "transformer");

        final List<ParserToken> flat = RepeatedOrSequenceParserTokenFlatParserTokenVisitor.flat(this);
        return flat.stream()
                .filter(SequenceParserToken::isNotWhitespace)
                .findFirst()
                .map(
                        t -> t.isSymbol() ?
                                this :
                                tryOperatorAwareBinaryOperator(flat, transformer)
                ).orElse(this);
    }

    private static boolean isNotWhitespace(final ParserToken token) {
        return false == token.isWhitespace();
    }

    private ParserToken tryOperatorAwareBinaryOperator(final List<ParserToken> tokens,
                                                       final BinaryOperatorTransformer transformer) {
        List<ParserToken> result = Lists.array();
        result.addAll(tokens);

        final int lowestOperatorPriority = transformer.lowestPriority();

        for (int priority = transformer.highestPriority(); priority >= lowestOperatorPriority; priority--) {
            boolean changed;

            do {
                changed = false;
                int i = 0;
                for (ParserToken t : result) {
                    if (0 != i && transformer.priority(t) == priority) {
                        changed = true;

                        final int begin = findNextNonWhitespace(result, i - 1, -1);
                        final int end = findNextNonWhitespace(result, i + 1, +1);

                        final List<ParserToken> binaryOperandTokens = Lists.array();
                        binaryOperandTokens.addAll(result.subList(begin, end + 1));

                        final List<ParserToken> withBinary = Lists.array();
                        withBinary.addAll(result.subList(0, begin));
                        withBinary.add(
                                transformer.binaryOperand(
                                        binaryOperandTokens,
                                        ParserToken.text(binaryOperandTokens),
                                        t
                                )
                        );
                        withBinary.addAll(result.subList(end + 1, result.size()));

                        result = withBinary;
                        break;
                    }
                    i++;
                }
            } while (changed && result.size() > 1);
        }

        return result.size() == 1 ?
                result.get(0) :
                ParserTokens.sequence(result, this.text());
    }

    private static int findNextNonWhitespace(final List<ParserToken> tokens,
                                             final int startIndex,
                                             final int step) {
        int i = startIndex;
        for (; ; ) {
            final ParserToken token = tokens.get(i);
            if (!token.isWhitespace()) {
                break;
            }
            i = i + step;
        }
        return i;
    }

    // children.........................................................................................................

    @Override
    public SequenceParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SequenceParserToken::new
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

}
