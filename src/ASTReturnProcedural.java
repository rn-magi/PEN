/* Generated By:JJTree: Do not edit this line. ASTBreakProcedural.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTReturnProcedural extends SimpleNode {
  public ASTReturnProcedural(int id) {
    super(id);
  }

  public ASTReturnProcedural(IntVParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(IntVParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=57acd554bcbcf4147950aed76afc2503 (do not edit this line) */
