package ast;

public class Relop extends Exp {
  public static final int EQ=0, NE=1, LT=2, LE=3, GT=4, GE=5;
  public int op;
  public Exp e1, e2;
  
  public Relop(int o, Exp ae1, Exp ae2) { op=o; e1=ae1; e2=ae2; }

  private void dumpOp(int op) {
    switch (op) {
    case EQ: DUMP("== "); break;
    case NE: DUMP("!= "); break;
    case LT: DUMP("< ");  break;
    case LE: DUMP("<= "); break;
    case GT: DUMP("> ");  break;
    case GE: DUMP(">= "); break;
    default: DUMP("?? ");
    }
  }

  public void dump() { 
    DUMP("(Relop "); dumpOp(op); DUMP(e1); DUMP(e2); DUMP(") ");
  }

  public void accept(VoidVI v) { v.visit(this); }
  public Type accept(TypeVI v) throws Exception { return v.visit(this); }
}
