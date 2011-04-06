package ast;

public class NewArray extends Exp {
  public Type et;
  public Int size;
  
  public NewArray(Type t, Int e) { et=t; size=e; }

  public void dump() { 
    DUMP("(NewArray "); DUMP(et); DUMP(size); DUMP(") ");
  }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
