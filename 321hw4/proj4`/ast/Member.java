package ast;

public class Member extends Exp {
  public Exp obj;
  public Id  var;
  
  public Member(Exp e, Id v) { obj=e; var=v; }

  public void dump() { 
    DUMP("(Member "); DUMP(obj); DUMP(var); DUMP(") ");
  }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
