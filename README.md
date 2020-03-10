[![Build Status](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser.svg?branch=master)](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-text-cursor-parser/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-text-cursor-parser?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-text-cursor-parser.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-text-cursor-parser.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser/alerts/)



# [Parser](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/Parser.java)
Parsers consume characters from a `TextCursor` source and creates tokens from chunks of text. There is one significant 
difference in operation between these Parsers and the parse methods (eg Long.parse) found in the JDK. The later requires
the boundaries of the token to be computed prior to parsing, with the source `String` extracted using some logic.

The Parser interface below denotes the main collaborators in the parsing process.

```java
public interface Parser<T extends ParserToken, C extends ParserContext> {

    /**
     * Attempts to parse the text given by the `TextCursor`.
     */
    Optional<T> parse(final TextCursor cursor, final C context);
}
```

The result of any parse attempt is an `Optional` because parsing often involves trying multiple alternatives until success.
The `ParserContext` may hold parser request state, such as `Locale` information so that numbers that are parsed can use
the correct decimal point character and more.



## [ParserToken](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/ParserToken.java)
Each `Parser` creates its own `ParserToken` sub class to hold the text consumed and the value it identified.



## [Error reporting](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/parser/ReportingParser.java)
In some cases a parse failure is an actual un-recoverable error, the most basic case is that all parsers have been tried
but text remains. This of course means, the `TextCursor` is positioned at an unrecognized character and an error should
be reported. A `ReportingParser` can be used to report an error in such cases. `TextCursor` and `TextCursorSavePoint`
also contain methods to return details of their respective positions, including the line number, column and line of text.



### [Tests](https://github.com/mP1/walkingkooka-text-cursor-parser/tree/master/src/test/java/walkingkooka/text/cursor/parser)
The test package contains many tests that accompany each and every parser and are a good read to better understand how
all the moving parts work and interact with other components.


## Dependencies

- walkingkooka
- junit

## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/walkingkooka-text-cursor-parser.git
```
