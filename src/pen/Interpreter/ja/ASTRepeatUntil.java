package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTRepeatUntil.java */

public class ASTRepeatUntil extends SimpleNode {
	public ASTRepeatUntil(int id) {
		super(id);
	}

	public ASTRepeatUntil(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
