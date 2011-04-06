package ast;

public class MethodDeclList extends List {
  public MethodDeclList() { super(); }
  public void add(MethodDecl n) { super.add(n); }
  public MethodDecl elementAt(int i)  { 
    return (MethodDecl)super.elementAt(i); 
  }
  public void accept(VoidVI v) { v.visit(this); }
  public void accept(TypeVI v) throws Exception { v.visit(this); }
}
