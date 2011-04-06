package ast;

public class Bool extends Exp {
  public int b;

  public Bool(int ab) { b=ab; }

  public String toString() { 
    if (b==0) return "false"; 
    else      return "true"; 
  }
  public void dump() { 
    if (b==0) DUMP("(False) ");
    else      DUMP("(True) ");
  }
  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) { return v.visit(this); }
}
