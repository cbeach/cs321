package ast;

public class BoolType extends Type {

  public String toString() { return "boolean"; }

  public void dump() { DUMP("(BoolType) "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) { return v.visit(this); }
}
