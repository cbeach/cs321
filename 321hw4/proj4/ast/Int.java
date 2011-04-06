package ast;

public class Int extends Exp {
  public int i;

  public Int(int ai) { i=ai; }

  public String toString() { return "int"; }

  public void dump() { DUMP("(Int " + i + ") "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) { return v.visit(this); }
}
