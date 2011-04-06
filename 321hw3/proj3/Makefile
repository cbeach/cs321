# Makefile for MINI compiler (v1.5). (Jingke Li)
#
JFLAGS = -g
JC = javac
JCC = javacc
RM = rm

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

all:	parser

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

parser/miniParser.java: parser/miniParser.jj
	(cd parser; $(JCC) miniParser.jj)

parser: ast parser/miniParser.class parser/TestParser.class

clean_parser:
	$(RM) parser/*.class parser/miniParser.java

ast: 	ast/ArrayElm.class ast/ArrayLen.class ast/ArrayType.class            \
	ast/Assign.class ast/Ast.class ast/Binop.class ast/Block.class	     \
	ast/Bool.class ast/BoolType.class ast/Call.class ast/CallStmt.class  \
	ast/ClassDecl.class ast/ClassDeclList.class ast/Exp.class            \
	ast/ExpList.class ast/Formal.class ast/FormalList.class ast/Id.class \
	ast/If.class ast/Int.class ast/IntType.class ast/List.class          \
	ast/Member.class ast/MethodDecl.class ast/MethodDeclList.class       \
	ast/Neg.class ast/NewArray.class ast/NewObj.class ast/Not.class      \
	ast/ObjType.class ast/Print.class ast/Program.class ast/Relop.class  \
	ast/Return.class ast/Stmt.class ast/StmtList.class ast/Text.class    \
	ast/This.class ast/Type.class ast/VarDecl.class ast/VarDeclList.class\
	ast/While.class

