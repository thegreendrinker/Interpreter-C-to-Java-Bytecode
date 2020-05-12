# Interpreter-C-to-Java-Bytecode
An interpreter which can convert simple C code in to Java Bytecode 

Note: this interpreter was designed to be
used in a Unix command line environment.

In order to test the separate programs enter
they first must be compiled
with:

javac "name of java file"

Then to run them:

java "name of class file" "name of test C file"


In order to create executable of Lexer.java:
Compile 
Token.java, TokenType.java, Lexer.java

Parser.java:
All Lexer related files, AbstractSyntax.java, Parser.java

StaticTypeCheck.java:
All Parser and Lexer related files, TypeMap.java, StaticTypeCheck.java

TypeTransformer.java:
All Parser, Lexer, and StaticTypeCheck related files, TypeTransformer.java

Semantics.java (The interpreter):
All Parser, Lexer, StaticTypeCheck, and TypeTransformer related files, State.java, Semantics.java
