package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTIfStat.java */

public class ASTIfStat extends SimpleNode {
	public ASTIfStat(int id) {
		super(id);
	}

	public ASTIfStat(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
