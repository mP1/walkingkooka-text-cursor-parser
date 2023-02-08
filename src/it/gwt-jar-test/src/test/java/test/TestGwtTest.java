package test;

import com.google.gwt.junit.client.GWTTestCase;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.j2cl.locale.LocaleAware;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.Locale;
import java.util.Optional;

@LocaleAware
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
                                        DateTimeContexts.fake(),
                                        DecimalNumberContexts.basic(
                                                "$",
                                                '.',
                                                "E",
                                                ',',
                                                '-',
                                                '%',
                                                '+',
                                                Locale.forLanguageTag("en-AU"),
                                                MathContext.DECIMAL32
                                        )
                                )
                        )
        );
    }
}
