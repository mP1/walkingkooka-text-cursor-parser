[![Build Status](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser.svg?branch=master)](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-text-cursor-parser/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-text-cursor-parser?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-text-cursor-parser.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-text-cursor-parser.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser/alerts/)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)



# [Parser](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/Parser.java)
A Parser consume characters from a `TextCursor` source returning a `ParserToken`. 

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



## Maven POM

```xml
<dependency>
    <groupId>walkingkooka</groupId>
    <artifactId>walkingkooka-text-cursor-parser</artifactId>
    <version>1.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```      



## Annotation processor arguments

See [j2cl-java-time](https://github.com/mP1/j2cl-java-time/blob/master/README.md#annotation-processor-arguments) about
required annotation processor arguments because of Parsers that return types such as `java.time.LocalDateTime`.



## j2cl-maven-compiler

Required `ignored-dependencies`.

```xml
<ignored-dependencies>
    <param>org.junit.jupiter:junit-jupiter-api:*</param>
    <param>org.junit.jupiter:junit-jupiter-engine:*</param>
    <param>org.junit.platform:junit-platform-commons:*</param>
    <param>org.junit.platform:junit-platform-engine:*</param>
</ignored-dependencies>
```



## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/walkingkooka-text-cursor-parser.git
```
