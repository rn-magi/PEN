package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTLong.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTLong extends SimpleNode {
  public ASTLong(int id) {
    super(id);
  }

  public ASTLong(IntVParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(IntVParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=64e60fd4d3ef8e03040c8ee5489f91c6 (do not edit this line) */
