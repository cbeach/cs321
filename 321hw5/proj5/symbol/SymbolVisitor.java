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
	private Id mainClass = null;
	private boolean hasMain = false;
	private boolean inClass = false;
	private boolean inMeth = false;
	private boolean multMain = false;
	// *** You may need other variables ***

	public SymbolVisitor() {
	symTable = new symbol.Table();
	}


	private void setupClassHierarchy(ClassDeclList cl) throws Exception 
	{
		Vector<ClassDecl> work = new Vector<ClassDecl>();
		Vector<String> done = new Vector<String>();
	
		for (int i=0; i<cl.size(); i++)
			work.add(cl.elementAt(i));
		
		while (work.size() > 0) 
		{
			for (int i=0; i<work.size(); i++) 
			{
				ClassDecl cd = (ClassDecl) work.elementAt(i);
				if (cd.pid != null) 
				{
					if (!done.contains(cd.pid.s)) continue;
						ClassRec cr = symTable.getClass(cd.cid);
					
					ClassRec pr = symTable.getClass(cd.pid);
					cr.linkParent(pr);
				}
	
				done.add(cd.cid.s);
				work.remove(cd);
			}
		}
	}



	public void visit(Program n) throws Exception 
	{
		n.cl.accept(this);
		if (!hasMain)
			throw new SymbolException("Method main is missing");
		if(mainClass != null)
			if(symTable.getClass(mainClass).varCnt() > 0)
				throw new SymbolException("main class are not allowed to have data fields");
		setupClassHierarchy(n.cl); // establish class hierarchy

	}

	public void visit(ClassDecl n) throws Exception
	{
	/*
		if(symTable.getClass(n.cid) != null)
		{	
			System.out.println("uh oh");
			throw new SymbolException("Class " + n.cid.s + " already defined");
		}
	*/
		try
		{
			mainClass = null;
			curClass = n.cid;
			symTable.addClass(n.cid);
			inClass = true;
			n.vl.accept(this);
			inClass = false;
			n.ml.accept(this);
			if(multMain == true)	
				throw new SymbolException("Method main already defined");
			
		}
		catch(java.lang.Exception e)
		{
			throw e;
		}
	}
	public void visit(MethodDecl n) throws Exception
	{
		curMeth = n.mid;

		ClassRec c = null;
		MethodRec m = null;
		
		if(n.mid.s.equals("main"))
		{
			if(mainClass != null)
				multMain = true;	
			else
			{
				hasMain = true;
				mainClass = curClass;
			}
		}
		try 
		{
			curMeth = n.mid;
			if(symTable.getClass(curClass).getMethod(n.mid) != null)
				throw new SymbolException("Method " + n.mid.s + " already defined");

			symTable.getClass(curClass).addMethod(curClass, n.mid, n.t);
			n.fl.accept(this);
			n.vl.accept(this);
			curMeth = null;

		}

		catch(symbol.SymbolException e)
		{
			throw e;
		}
		catch(java.lang.Exception e1){}
		
	}
	public void visit(VarDecl n) throws Exception
	{

		try
		{
			
			if(inClass == true)
			{

				if(symTable.getClass(curClass).getClassVar(n.var) != null)
					throw new SymbolException("ClassVar " + n.var.s + " already defined");
			
			}
			else
			{
				if(curMeth != null)
				{
					if(symTable.getClass(curClass).getMethod(curMeth).getParam(n.var) != null)
						throw new SymbolException( n.var.s + " already defined as a param");
					
				}

			}
			if(inClass == true)
				symTable.getClass(curClass).addClassVar(n.var, n.t, n.e);
			else
				symTable.getClass(curClass).getMethod(curMeth).addLocal(n.var, n.t);
		}	
		
		catch(symbol.SymbolException e)
		{
			throw e;
		}
	}


	public void visit(Formal n) throws Exception
	{
		try
		{
			if(symTable.getClass(curClass).getMethod(curMeth).getParam(n.id) != null)
				throw new SymbolException("Param " + n.id.s + " already defined");
			if(symTable.getClass(curClass).getMethod(curMeth).getLocal(n.id) != null)
				throw new SymbolException(n.id.s + " already defined as a param");
			symTable.getClass(curClass).getMethod(curMeth).addParam(n.id, n.t);
		}
		catch(symbol.SymbolException e)
		{
			throw e;
		}
	
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
