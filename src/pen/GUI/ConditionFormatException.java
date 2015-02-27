package pen.GUI;
public class ConditionFormatException extends NumberFormatException {
	public int line;

	public ConditionFormatException () {
		super();
	}

	public ConditionFormatException (int line) {
		super();
		this.line = line;
	}
}
