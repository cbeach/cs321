package ast;

public class IntType extends Type {

  public String toString() { return "int"; }

  public void dump() { DUMP("(IntType) "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) { return v.visit(this); }
}
