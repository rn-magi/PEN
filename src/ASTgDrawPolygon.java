/* Generated By:JJTree: Do not edit this line. ASTgDrawPolygon.java */

public class ASTgDrawPolygon extends SimpleNode {
  public ASTgDrawPolygon(int id) {
    super(id);
  }

  public ASTgDrawPolygon(IntVParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(IntVParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
