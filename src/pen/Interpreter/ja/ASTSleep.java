package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTSleep.java */

public class ASTSleep extends SimpleNode {
  public ASTSleep(int id) {
    super(id);
  }

  public ASTSleep(IntVParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(IntVParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
