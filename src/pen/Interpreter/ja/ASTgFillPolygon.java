package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTgFillPolygon.java */

public class ASTgFillPolygon extends SimpleNode {
  public ASTgFillPolygon(int id) {
    super(id);
  }

  public ASTgFillPolygon(IntVParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(IntVParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
