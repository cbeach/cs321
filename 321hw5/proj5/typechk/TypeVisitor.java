// SymbolVisitor.java --- Processing declarations and creating symbol tables
// for MINI 1.5.
//
// Casey Beach
//
package typechk;
import ast.*;
import symbol.*;
import java.util.Hashtable;
import java.util.Vector;

public class TypeVisitor implements TypeVI {

	private Table symTable;  // the top-scope symbol table
	private ClassRec currClass;
	private ClassRec memberClass;
	private MethodRec currMethod;
	private boolean hasReturn;
	
	public TypeVisitor(Table symtab){
		symTable = symtab;
	}

	public void visit(Program n) throws Exception {
		n.cl.accept(this);
	}


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

	// DECLS

	public void visit(ClassDecl n) throws Exception{
		try{
			currClass = symTable.getClass(n.cid);	
			
			if(n.ml != null)
				n.ml.accept(this);
			
			if(n.vl != null)
				n.vl.accept(this);

		}
		catch(symbol.SymbolException e){
			throw e;
		}
		
	}
	
	public void visit(MethodDecl n) throws Exception {
		currMethod = currClass.getMethod(n.mid);
//		System.out.println(n.mid.s);
		try{
			n.fl.accept(this);
			n.vl.accept(this);
			n.sl.accept(this);
		}
		catch(symbol.SymbolException e){
			throw e;
		}
		catch(typechk.TypeException e){
			throw e;
		}
		
		if(currMethod != null) {
			if(!hasReturn && currMethod.rtype() != null) {
				throw new TypeException("Method " + currMethod.id().s + " is missing a Return statment");
			}
		}
		hasReturn = false;
	
	}
	
	public void visit(VarDecl n) throws Exception{

		if(n.e != null){
			n.e.accept(this);
		}
		if(n.t instanceof ObjType){
			ObjType temp = (ObjType)n.t;
			if(symTable.getClass(temp.cid) == null)			
				throw new SymbolException("Var " + n.var.s + " not defined");
		}
		else if(currClass.getClassVar(n.var) == null
		&& currMethod.getLocal(n.var) == null) {
			throw new SymbolException("Var " + n.var.s + " not defined");
		}
	}
	
	public void visit(Formal n) throws Exception {
		if(n.t instanceof ObjType){
			ObjType temp = (ObjType)n.t;
			if(symTable.getClass(temp.cid) == null)		
				throw new SymbolException("Class " + n.id.s + " not defined");
		}
		else if(currClass.getClassVar(n.id) == null
		&& currMethod.getLocal(n.id) == null
		&& currMethod.getParam(n.id) == null) {
			throw new SymbolException("Var " + n.id.s + " not defined");
		}
	}	

	// TYPES --- use the nodes themselves 

	public Type visit(IntType n) { return n; }
	public Type visit(BoolType n) { return n; }
	public Type visit(ObjType n) throws Exception { return n; }
	public Type visit(ArrayType n) { return n; }

	// No need to process statements or expressions nodes.

	public void visit(StmtList n) throws Exception {
		for(int i = 0; i < n.size(); i++)
			n.elementAt(i).accept(this);
			
	}
	public void visit(Block n) throws Exception {
		n.sl.accept(this);
	}
	
	public void visit(Assign n) throws Exception {
		Type t1 = n.lhs.accept(this);
		Type t2 = n.rhs.accept(this);
		
		if(!compatible(t1,t2))
			throw new TypeException("Incompatible types in Assign: " + t1.toString() + " <- " + t2.toString());
	}
	public void visit(CallStmt n) throws Exception {
		n.obj.accept(this);
		n.args.accept(this);
		ClassRec c1 = null;
		Type t1 = null, t2 = null;
		if(n.obj instanceof This) {
			
			MethodRec classMethod = currClass.getMethod(n.mid);

			if(classMethod == null) {

				c1 = currClass;
				while(c1.parent() != null){
					c1 = c1.parent();
					classMethod = c1.getMethod(n.mid);
					if(classMethod != null){
						break;
					}
				}
				if(classMethod == null)
					throw new SymbolException("Method " + n.mid.s + " not defined");
			}
			if(n.args.size() != classMethod.paramCnt()){
				throw new TypeException("Formals' and actuals' counts don't match: " + classMethod.paramCnt() + " vs. " + n.args.size());
			}						
			for(int i = 0; i < n.args.size(); i++) {
				t1 = n.args.elementAt(i).accept(this);
				t2 = classMethod.getParamAt(i).type();
				if(!(compatible(t1, t2)))
					throw new TypeException("Formal's and actual's types not compatible: " +
					t1.toString() + " vs. " + t2.toString());

			}

		}
		else {
			
			VarRec varId = currMethod.getLocal(((Id)n.obj));
			ClassRec varClass = symTable.getClass(new Id(varId.type().toString()));
			MethodRec classMethodId = varClass.getMethod(n.mid);

			MethodRec method = currClass.getMethod(n.mid);
			
			if(method == null && classMethodId == null){

				c1 = varClass;
				while(c1.parent() != null){
					c1 = c1.parent();
					classMethodId = c1.getMethod(n.mid);
					if(classMethodId != null)
						return;
				}
					
				throw new SymbolException("Var " + n.mid.s + " not defined");
				

			}
			if(method != null && n.args.size() != method.paramCnt())
				throw new TypeException("Formals' and actuals' counts don't match: " + method.paramCnt() + " vs. " + n.args.size());
			for(int i = 0; i < n.args.size(); i++) {
				t1 = n.args.elementAt(i).accept(this);
				t2 = classMethodId.getParamAt(i).type();
				if(!(compatible(t1, t2)))
					throw new TypeException("Formals' and actuals' types not compatible: " +
					t2.toString() + " vs. " + t1.toString());

			}

		}
/*
		for(int i = 0; i < n.args.size(); i++) {
			t1 = n.args.elementAt(i).accept(this);
			t2 = classMethodId.getParamAt(i).type();
			if(!(compatible(t1, t2)))
				throw new TypeException("Formals' and actuals' types not compatible: " +
				t2.toString() + " vs. " + t1.toString());
		}
*/
	}
	public void visit(If n) throws Exception {
		Type t1 = n.e.accept(this);

		if(!(t1 instanceof BoolType))
			throw new TypeException("Test of If is not of boolean type: " + t1.toString());

		n.s1.accept(this);
		if(n.s2 != null)
			n.s2.accept(this);
		
	}
	public void visit(While n) throws Exception {
		Type t1 = n.e.accept(this);

		if(!(t1 instanceof BoolType))
			throw new TypeException("Test of while is not of boolean type: " + t1.toString());

		n.s.accept(this);
	}
	public void visit(Print n) throws Exception {
//		System.out.println("Print");
		Type nType;

		if(n.e != null) {
			nType = n.e.accept(this);

			if(!(nType instanceof IntType
			|| nType instanceof BoolType))
			{
				throw new TypeException("Argument to Print must be an int or boolean or a String :" 
				+ nType.toString());
			}
		}

	
	}
	public void visit(Return n) throws Exception {
		
		Type t1 = null;
		if(n.e != null){
			t1 = n.e.accept(this);
		}
		else {
			throw new TypeException("Return is missing an expr of type " + currMethod.rtype().toString());
		}

		if(!(t1.toString().equals(currMethod.rtype().toString())))
			throw new TypeException("Wrong return type for method " + currMethod.id().s + ":" + t1.toString());
		
		hasReturn = true;

	}

	public void visit(ExpList n) throws Exception {
		for(int i = 0; i < n.size(); i++)
			n.elementAt(i).accept(this);
	}
	public Type visit(Binop n) throws Exception { 
		Type t1 = n.e1.accept(this);
		Type t2 = n.e2.accept(this);
		switch(n.op){
			case 0://ADD 	
				if(t1 instanceof IntType && t2 instanceof IntType)
					return new IntType();
				else
					throw new TypeException("Operands in arith op must be int: " + t1.toString()
					+ "+" + t2.toString());
			case 1: //SUB
				if(t1 instanceof IntType && t2 instanceof IntType)
					return new IntType();
				else
					throw new TypeException("Operands in arith op must be int: " + t1.toString()
					+ "-" + t2.toString());
			case 2: //MULT
				if(t1 instanceof IntType && t2 instanceof IntType)
					return new IntType();
				else
					throw new TypeException("Operands in arith op must be int: " + t1.toString()
					+ "*" + t2.toString());
			case 3: //DIVIDE
				if(t1 instanceof IntType && t2 instanceof IntType)
					return new IntType();
				else
					throw new TypeException("Operands in arith op must be int: " + t1.toString()
					+ "/" + t2.toString());
			case 4: //AND
				if(t1 instanceof BoolType && t2 instanceof BoolType)
					return new BoolType();
				else
					throw new TypeException("Operands in logical op must be booleans: " 
					+ t1.toString() + "&&" + t2.toString());
			case 5: //OR 	
				if(t1 instanceof BoolType && t2 instanceof BoolType)
					return new BoolType();
				else
					throw new TypeException("Operands in logical op must be booleans: " 
					+ t1.toString() + "||" + t2.toString());
		}
		return t1;

		
		 
	}
	public Type visit(Relop n) throws Exception { 
		Type t1 = n.e1.accept(this);
		Type t2 = n.e2.accept(this);
		if(t1 != null & t2 != null ) {
			switch(n.op){
				case 0:	
					if(!compatible(t1,t2)){
						throw new TypeException("Incorrect operand types in Relop: " 
						+ t1.toString() + "==" + t2.toString());		
					}
				case 1:
					
					if(!compatible(t1,t2)){
						
						throw new TypeException("Incorrect operand types in Relop: " 
						+ t1.toString() + "!=" + t2.toString());
					}
				case 2:	
					
					if(!compatible(t1,t2))
						throw new TypeException("Incorrect operand in types Relop: " 
						+ t1.toString() + "<" + t2.toString());
				case 3:	

					if(!compatible(t1,t2))
						throw new TypeException("Incorrect operand types in Relop: " 
						+ t1.toString() + "<=" + t2.toString());	
				case 4:	

					if(!compatible(t1,t2))
						throw new TypeException("Incorrect operand types in Relop: " 
						+ t1.toString() + ">" + t2.toString());
				case 5:	

					if(!compatible(t1,t2))
						throw new TypeException("Incorrect operand types in Relop: " 
						+ t1.toString() + ">=" + t2.toString());
			}
		}
		return new BoolType();
	}
	public Type visit(Neg n) throws Exception { 
		Type t1 = n.e.accept(this);

		if(t1 instanceof IntType)
			return t1;
		else{
			throw new TypeException("Neg expression requires boolean: " + t1.toString());
		}
	}
	public Type visit(Not n) throws Exception { 
		Type t1 = n.e.accept(this);

		if(t1 instanceof BoolType)
			return t1;
		else{
			throw new TypeException("Not expression requires boolean: " + t1.toString());
		}
	}
	public Type visit(ArrayElm n) throws Exception { 
		Type t1 = n.array.accept(this);
		Type t2 = n.idx.accept(this);

		if(!(t1 instanceof ArrayType)){
			throw new TypeException("ArrayElm object is not an array: " + t1.toString());
		}

		if(t2 instanceof IntType){
			return ((ArrayType)t1).et;
		} else
			throw new TypeException("Array index is not an int: " + t2.toString());
	}

	public Type visit(ArrayLen n) throws Exception { 
	
		return new IntType(); 
	}

	public Type visit(Member n) throws Exception { 
		
		Type t1 = n.obj.accept(this); 

		ClassRec c1 = null;

		if(!(t1 instanceof ObjType))
			throw new TypeException("Object in Member is not ObjType: " + t1.toString());
		
		if(n.obj instanceof This) {
			
			VarRec classVar = currClass.getClassVar(n.var);
			
			if(classVar == null) {

				c1 = currClass;
				while(c1.parent() != null){
					c1 = c1.parent();
					classVar = c1.getClassVar(n.var);
					if(classVar != null)
						return classVar.type();
				}

				throw new SymbolException("No Var " + classVar.id().s + " in This");
			}
			else{
				return classVar.type();
			}
		}
			

		VarRec varId = currMethod.getLocal(((Id)n.obj));
		ClassRec varClass = symTable.getClass(new Id(varId.type().toString()));
		VarRec classVarId = varClass.getClassVar(n.var);

		if(classVarId != null) {

			return classVarId.type();
		}
		else {

			c1 = varClass;
			while(c1.parent() != null){
				c1 = c1.parent();
				classVarId = c1.getClassVar(n.var);
				if(classVarId != null)
					return classVarId.type();
			}
				
			throw new SymbolException("Var " + n.var.s + " not defined");
		}

	}

	public Type visit(Call n) throws Exception {
		
		n.obj.accept(this);
		n.args.accept(this);
		ClassRec c1 = null;
		Type t1 = null, t2 = null;

		if(n.obj instanceof This) {
			
			MethodRec classMethod = currClass.getMethod(n.mid);
			
			if(classMethod == null) {
				
				c1 = currClass;
				while(c1.parent() != null){
					c1 = c1.parent();
					classMethod = c1.getMethod(n.mid);
					if(classMethod != null){
						break;
					}
				}
				if(classMethod == null)
					throw new SymbolException("Method " + n.mid.s + " not defined");
				if(n.args.size() != classMethod.paramCnt()){
					throw new TypeException("Formals' and actuals' counts don't match: " + classMethod.paramCnt() + " vs. " + n.args.size());
				}						
				for(int i = 0; i < n.args.size(); i++) {
					t1 = n.args.elementAt(i).accept(this);
					t2 = classMethod.getParamAt(i).type();
					if(!(compatible(t1, t2)))
						throw new TypeException("Formal's and actual's types not compatible: " +
						t1.toString() + " vs. " + t2.toString());
	
				}

				return classMethod.rtype();

			}
			else{
				if(n.args.size() != classMethod.paramCnt())
					throw new TypeException("Formals' and actuals' counts don't match: " + classMethod.paramCnt() + " vs. " + n.args.size());
				for(int i = 0; i < n.args.size(); i++) {
					t1 = n.args.elementAt(i).accept(this);
					t2 = classMethod.getParamAt(i).type();
					if(!(compatible(t2, t1)))
						throw new TypeException("Formal's and actual's types not compatible: " +
						t2.toString() + " vs. " + t1.toString());
				}


				return classMethod.rtype();
			}
		}
		else {

			VarRec varId = currMethod.getLocal(((Id)n.obj));
			ClassRec varClass = symTable.getClass(new Id(varId.type().toString()));
			MethodRec classMethodId = varClass.getMethod(n.mid);

			MethodRec method = currClass.getMethod(n.mid);
			
			if(method == null && classMethodId == null){

				if(classMethodId != null) {

					return classMethodId.rtype();
				}
				else {

					c1 = varClass;
					while(c1.parent() != null){
						c1 = c1.parent();
						classMethodId = c1.getMethod(n.mid);
						if(classMethodId != null)
							return classMethodId.rtype();
					}
						
					throw new SymbolException("Var " + n.mid.s + " not defined");
				}

			}
			
			if(method != null && n.args.size() != method.paramCnt())
				throw new TypeException("Formals' and actuals' counts don't match: " + method.paramCnt() + " vs. " + n.args.size());
			if(method != null){
				for(int i = 0; i < n.args.size(); i++) {
					t1 = n.args.elementAt(i).accept(this);
					
					t2 = method.getParamAt(i).type();

					if(!(compatible(t2, t1)))
						throw new TypeException("Formals' and actuals' types not compatible: " +
						t2.toString() + " vs. " + t1.toString());

				}

				return method.rtype();
			}
			else
				return classMethodId.rtype();
			
		}
	}
	
	public Type visit(NewArray n) throws Exception { 
		if(n.et instanceof BoolType)
			return new ArrayType(new BoolType());
		else if(n.et instanceof IntType)
			return new ArrayType(new IntType());
		else if(n.et instanceof ObjType)
			return new ArrayType(new ObjType(((ObjType)n.et).cid));
		else
			throw new TypeException("Array must be of type Obj, int or bool");
	
	}
 	
	public Type visit(NewObj n) throws Exception {
		if(n.args != null)
			n.args.accept(this);

		ClassRec calledClass = symTable.getClass(n.cid);
		if(calledClass == null){
			throw new SymbolException( "Class " + n.cid.s + " not difined");
		}
		return new ObjType(calledClass.id()); 
	}
	public Type visit(Id n) throws Exception { 

		VarRec currVar = currClass.getClassVar(n);
		if(currVar == null)
			currVar = currMethod.getLocal(n);
		if(currVar == null)
			currVar = currMethod.getParam(n);
		if(currVar == null)
			throw new SymbolException("Var " + n.s + " not defined");

//		System.out.println("Id " + n.s);
		return currVar.type();
		
	}
	public Type visit(This n) throws Exception { 
		
		return new ObjType(currClass.id()); 
	}
/*
	public Type visitExp(Exp exp) throws Exception{
		try
		{
			if(exp instanceof Binop){ return visit((Binop)exp);}
			else if(exp instanceof Relop){ return visit((Relop)exp);}
			else if(exp instanceof Neg){ return visit((Neg)exp);}
			else if(exp instanceof Not){ return visit((Not)exp);}
			else if(exp instanceof ArrayElm){ return visit((ArrayElm)exp);}
			else if(exp instanceof ArrayLen){ return visit((ArrayLen)exp);}
			else if(exp instanceof Member){ return visit((Member)exp);}
			else if(exp instanceof Call){ return visit((Call)exp);}
			else if(exp instanceof NewArray){ return visit((NewArray)exp);}
			else if(exp instanceof NewObj){ return visit((NewObj)exp);}
			else if(exp instanceof Id){ return visit((Id)exp);}
			else if(exp instanceof This){ return visit((This)exp);}

	
		}

		catch(typechk.TypeException e){
			throw e;
		}
		catch(symbol.SymbolException e){
			throw e;
		}


		return null;
	}
*/
	public Type visit(Int n) { return new IntType(); }
	public Type visit(Bool n) { return new BoolType(); }
	public Type visit(Text n) { return new IntType(); }

	public boolean compatible(Type t1, Type t2) throws Exception {

		ClassRec tempCR = null;
		ClassRec o1 = null;
		ClassRec o2 = null;
		ClassRec tempO = null;	//making this working var so I can move up the inhereitence tree properly.

		if(t1 != null && t2 != null){
			if(t1 instanceof IntType && t2 instanceof IntType)
				return true;
			if(t1 instanceof BoolType && t2 instanceof BoolType)
				return true;
		
			if(t1 instanceof ObjType && t2 instanceof ObjType){
			
				o1 = symTable.getClass(((ObjType)t1).cid);
				o2 = symTable.getClass(((ObjType)t2).cid);
				
				if(o1 != null && o2 != null){	
					if(t1.toString().equals(t2.toString())){
						return true;
					}
					else{
						while(o2.parent() != null) {
							o2 = o2.parent();
				
							if(o1.id().s.equals(o2.id().s))
								return true;
						}


						return false;
					}
				}
			}
			if(t1 instanceof ArrayType && t2 instanceof ArrayType)
				return true;
		}
		return false;


	}


}
