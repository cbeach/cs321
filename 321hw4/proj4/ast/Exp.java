package ast;

public abstract class Exp extends Ast {
  public abstract Type accept(TypeVI v) throws Exception;
}
