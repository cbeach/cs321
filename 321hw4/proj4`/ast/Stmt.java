package ast;

public abstract class Stmt extends Ast {
  public abstract void accept(TypeVI v) throws Exception;
}
