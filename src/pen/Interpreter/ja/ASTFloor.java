package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTFloor.java */

public class ASTFloor extends SimpleNode {
	public ASTFloor(int id) {
		super(id);
	}

	public ASTFloor(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
