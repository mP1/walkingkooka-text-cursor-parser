package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.Locale;
import java.util.Optional;

@walkingkooka.j2cl.locale.LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
            1,
            1
        );
    }

    public void testParserBigInteger() {
        final String text = "123";

        assertEquals(
            Optional.of(
                ParserTokens.bigInteger(
                    BigInteger.valueOf(123),
                    text
                )
            ),
            Parsers.bigInteger(10)
                .parse(TextCursors.charSequence(text),
                    ParserContexts.basic(
                        InvalidCharacterExceptionFactory.POSITION,
                        DateTimeContexts.fake(),
                        DecimalNumberContexts.basic(
                            false, // false == canNumbersHaveGroupSeparator
                            DecimalNumberSymbols.with(
                                '-',
                                '+',
                                '0',
                                "$",
                                '.',
                                "E",
                                ',',
                                "Infinity",
                                '.',
                                "Nan",
                                '%',
                                '\u2030'
                            ),
                            Locale.forLanguageTag("en-AU"),
                            MathContext.DECIMAL32
                        )
                    )
                )
        );
    }
}
