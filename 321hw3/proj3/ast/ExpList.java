package ast;

public class ExpList extends List {
  public ExpList() { super(); }
  public void add(Exp n) { super.add(n); }
  public void addAll(ExpList el) { super.addAll(el); }
  public Exp elementAt(int i)  { return (Exp)super.elementAt(i); }

  public void accept(VoidVI v) { v.visit(this); }
  public void accept(TypeVI v) throws Exception { v.visit(this); }
}
