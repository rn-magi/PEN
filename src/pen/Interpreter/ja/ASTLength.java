package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTLength.java */

public class ASTLength extends SimpleNode {
	public ASTLength(int id) {
		super(id);
	}

	public ASTLength(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
