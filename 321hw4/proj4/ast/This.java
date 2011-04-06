package ast;

public class This extends Exp {

  public void dump() { DUMP("(This) "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
