package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTArrayNum.java */

public class ASTArrayNum extends SimpleNode {
	public ASTArrayNum(int id) {
		super(id);
	}

	public ASTArrayNum(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
