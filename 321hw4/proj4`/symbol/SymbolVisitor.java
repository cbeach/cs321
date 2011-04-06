// SymbolVisitor.java --- Processing declarations and creating symbol tables
// for MINI 1.5.
//
// Casey Beach
//
package symbol;
import ast.*;
import java.util.Hashtable;
import java.util.Vector;

public class SymbolVisitor implements TypeVI {
	public Table symTable;  // the top-scope symbol table
	private Id curClass;
	private Id curMeth;

	// *** You may need other variables ***

	public SymbolVisitor() {
	symTable = new symbol.Table();
	}


	public void visit(Program n) 
	{
		try
		{
			n.cl.accept(this);
		}
		catch(java.lang.Exception e){}
	}
	public void visit(ClassDecl n)
	{
		try
		{	
			curClass = n.cid;
			symTable.addClass(n.cid);
			n.vl.accept(this);
			n.ml.accept(this);
		}
		catch(symbol.SymbolException e){}
		catch(java.lang.Exception e1){}
	}
	public void visit(MethodDecl n)
	{
		curMeth = n.mid;

		ClassRec c = null;
		MethodRec m = null;

		try 
		{
			symTable.getClass(curClass).addMethod(curClass, n.mid, n.t);
			n.fl.accept(this);
			n.vl.accept(this);

		}

		catch(symbol.SymbolException e){}
		catch(java.lang.Exception e1){}
		
	}
	public void visit(VarDecl n)
	{
		try
		{
			symTable.getClass(curClass).getMethod(curMeth).addLocal(n.var, n.t);
		}
		catch(symbol.SymbolException e){}
	}


	public void visit(Formal n)
	{
		try
		{
			symTable.getClass(curClass).getMethod(curMeth).addParam(n.id, n.t);
		}
		catch(symbol.SymbolException e){}
	
	}

	// *** This is the part you need to fill ***


	// LISTS --- use default traversal

	public void visit(List n) throws Exception {}

	public void visit(ClassDeclList n) throws Exception {
		for (int i = 0; i < n.size(); i++)
			n.elementAt(i).accept(this);
	}

	public void visit(VarDeclList n) throws Exception {
		for (int i = 0; i < n.size(); i++)
			n.elementAt(i).accept(this);
	}

	public void visit(MethodDeclList n) throws Exception {
		for (int i = 0; i < n.size(); i++)
			n.elementAt(i).accept(this);
	}

	public void visit(FormalList n) throws Exception {
		for (int i = 0; i < n.size(); i++)
			n.elementAt(i).accept(this);
	}

	// TYPES --- use the nodes themselves 

	public Type visit(IntType n) { return n; }
	public Type visit(BoolType n) { return n; }
	public Type visit(ObjType n) throws Exception { return n; }
	public Type visit(ArrayType n) { return n; }

	// No need to process statements or expressions nodes.

	public void visit(StmtList n) throws Exception {}
	public void visit(Block n) throws Exception {}
	public void visit(Assign n) throws Exception {}
	public void visit(CallStmt n) throws Exception {}
	public void visit(If n) throws Exception {}
	public void visit(While n) throws Exception {}
	public void visit(Print n) throws Exception {}
	public void visit(Return n) throws Exception {}

	public void visit(ExpList n) throws Exception {}
	public Type visit(Binop n) throws Exception { return null; }
	public Type visit(Relop n) throws Exception { return null; }
	public Type visit(Neg n) throws Exception { return null; }
	public Type visit(Not n) throws Exception { return null; }
	public Type visit(ArrayElm n) throws Exception { return null; }
	public Type visit(ArrayLen n) throws Exception { return null; }
	public Type visit(Member n) throws Exception { return null; }
	public Type visit(Call n) throws Exception { return null; }
	public Type visit(NewArray n) throws Exception { return null; }
	public Type visit(NewObj n) throws Exception { return null; }
	public Type visit(Id n) throws Exception { return null; }
	public Type visit(This n) throws Exception { return null; }

	public Type visit(Int n) { return null; }
	public Type visit(Bool n) { return null; }
	public Type visit(Text n) { return null; }

}
