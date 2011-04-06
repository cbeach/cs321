package ast;

public class Neg extends Exp {
  public Exp e;
  
  public Neg(Exp ae) { e=ae; }

  public void dump() { DUMP("(Neg "); DUMP(e); DUMP(") "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
