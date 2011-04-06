package ast;

public class Not extends Exp {
  public Exp e;
  
  public Not(Exp ae) { e=ae; }

  public void dump() { DUMP("(Not "); DUMP(e); DUMP(") "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
