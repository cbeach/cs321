package ast;

public class FormalList extends List {
  public FormalList() { super(); }
  public void add(Formal n) { super.add(n); }
  public Formal elementAt(int i)  { 
    return (Formal)super.elementAt(i); 
  }
  public void accept(VoidVI v) { v.visit(this); }
  public void accept(TypeVI v) throws Exception { v.visit(this); }
}
