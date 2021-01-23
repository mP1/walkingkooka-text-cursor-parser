[![Build Status](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser.svg?branch=master)](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-text-cursor-parser/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-text-cursor-parser?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-text-cursor-parser.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-text-cursor-parser.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser/alerts/)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)



# [Parser](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/Parser.java)
A Parser consumes characters from a `TextCursor` source returning a `ParserToken` when successful. 

```java
public interface Parser<T extends ParserToken, C extends ParserContext> {

    /**
     * Attempts to parse the text given by the `TextCursor`.
     */
    Optional<T> parse(final TextCursor cursor, final C context);
}
```

If parsing is successful into a `Optional` wrapped `ParserToken` and the `TextCursor` is also advanced. This approach
allows multiple alternatives to be tried until success. Complex compositions may be created with changing settings such
as `Locale` aware values coming from a `ParserContext`.



## [Error reporting](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/parser/ReportingParser.java)

In some cases a parse failure is an actual un-recoverable error, the most basic case is that all parsers have been tried
but text remains. This of course means, the `TextCursor` is positioned at an unrecognized character and an error should
be reported. A `ReportingParser` can be used to report an error in such cases. `TextCursor` and `TextCursorSavePoint`
also contain methods to return details of their respective positions, including the line number, column and line of text.



## [ParserTesting](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/parser/ParserTesting.java)

This interface contains numerous default methods which may be mixed in to any TestCase wishing to test a `Parser`. Examine 
the [test cases](https://github.com/mP1/walkingkooka-text-cursor-parser/tree/master/src/test/java/walkingkooka/text/cursor/parser) for a few more advanced examples 

- Tests become very succinct with little boilerplate.
- Numerous overloads, supporting tests with minimum parameters, other requirements are defaulted.
- The defaults come from factory methods.
- `parseAndCheck` includes numerous asserts including a handy tree dump view when the result `ParserToken` does not match the expected. This view becomes particularly helpful when the ParserToken graph has many tokens.

The dump print out view below shows a `SequenceParserToken` with 3 children. Each parent has its children indented and nested.

```
Sequence
  String="a1" a1 (java.lang.String)
  BigDecimal="1.5" 1.5 (java.math.BigDecimal)
  BigInteger="23" 23 (java.math.BigInteger)

```



## [Tests](https://github.com/mP1/walkingkooka-text-cursor-parser/tree/master/src/test/java/walkingkooka/text/cursor/parser)

The test source contains many tests that accompany each and every parser and are a good read to better understand how
all the moving parts work and interact with other components.



## JDK static parser replacements

Parsers are available for the follow static methods:

- Byte.parseByte
- Double.parseDouble
- Float.parseFloat
- Integer.parseInt
- LocalDate.parse
- LocalDateTime.parse
- LocalTime.parse
- Long.parseLong
- Short.parseShort



## Related projects 

- [Parser combinators](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf)
- [EBNF -> CharPredicate](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate)



