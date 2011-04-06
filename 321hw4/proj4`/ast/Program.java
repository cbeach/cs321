package ast;

public class Program extends Ast {
  public ClassDeclList cl;

  public Program(ClassDeclList acl) { cl=acl; }
 
  public void dump() { 
    DUMP("(Program "); DUMP("ClassDeclList\n", cl); DUMP(")\n"); 
  }

  public void accept(VoidVI v)  { v.visit(this); }
  public void accept(TypeVI v) throws Exception { v.visit(this); }
}
