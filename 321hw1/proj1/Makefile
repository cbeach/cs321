# Makefile for MINI compiler (v1.5). (Jingke Li)
#
JFLAGS = -g
JC = javac
JCC = javacc

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

lexer/miniLexer.java: lexer/miniLexer.jj
	(cd lexer; $(JCC) miniLexer.jj)

lexer: 	lexer/miniLexer.class lexer/TestLexer.class

clean_lexer:
	$(RM) lexer/*.class lexer/miniLexer.java
