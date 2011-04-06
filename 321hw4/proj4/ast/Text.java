package ast;

public class Text extends Exp {
  public String s;

  public Text(String as) { s=as; }

  public void dump() { DUMP("(Text " + s + ") "); }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) { return v.visit(this); }
}
