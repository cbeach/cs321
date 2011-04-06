# Makefile for MINI compiler (v1.5). (Jingke Li)
#
JFLAGS = -g
JC = javac
JCC = javacc
RM = rm

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

all:	parser0

#lexer/miniLexer.java: lexer/miniLexer.jj
#	(cd lexer; $(JCC) miniLexer.jj)

#lexer: 	lexer/miniLexer.class lexer/TestLexer.class

#clean_lexer:
#	$(RM) lexer/*.class lexer/miniLexer.java


#lexer/miniLexer.java: lexer/miniLexer.jj
#	(cd lexer; $(JCC) miniLexer.jj)

parser0/miniParser0.java: parser0/miniParser0.jj
	(cd parser0; $(JCC) miniParser0.jj)

parser0: parser0/miniParser0.class parser0/TestParser0.class

clean_parser0:
	$(RM) parser0/*.class parser0/miniParser0.java

