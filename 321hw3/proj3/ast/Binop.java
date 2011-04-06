package ast;

public class Binop extends Exp {
  public static final int ADD=0, SUB=1, MUL=2, DIV=3, AND=4, OR=5;
  public int op;
  public Exp e1, e2;
  
  public Binop(int o, Exp ae1, Exp ae2) { op=o; e1=ae1; e2=ae2; }

  private void dumpOp(int op) {
    switch (op) {
    case ADD: DUMP("+ "); break;
    case SUB: DUMP("- "); break;
    case MUL: DUMP("* "); break;
    case DIV: DUMP("/ "); break;
    case AND: DUMP("&& "); break;
    case OR:  DUMP("|| "); break;
    default:  DUMP("?");
    }
  }

  public void dump() { 
    DUMP("(Binop "); dumpOp(op); DUMP(e1); DUMP(e2); DUMP(") ");
  }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
