/* Generated By:JJTree: Do not edit this line. ASTLabel.java */

public class ASTLabel extends SimpleNode {
  public ASTLabel(int id) {
    super(id);
  }

  public ASTLabel(IntVParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(IntVParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
