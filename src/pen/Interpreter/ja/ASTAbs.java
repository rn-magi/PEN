package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTAbs.java */

public class ASTAbs extends SimpleNode {
	public ASTAbs(int id) {
		super(id);
	}

	public ASTAbs(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
