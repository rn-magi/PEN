package pen.Interpreter.ja;
/* Generated By:JJTree: Do not edit this line. ASTPutStat.java */

public class ASTPutStat extends SimpleNode {
	public String n = "";
	
	public ASTPutStat(int id) {
		super(id);
	}

	public ASTPutStat(IntVParser p, int id) {
		super(p, id);
	}


	/** Accept the visitor. **/
	public Object jjtAccept(IntVParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
