public class VarNameError extends Error{
	public String message;
	public int  line;

	public VarNameError() {
	}

	public VarNameError(String _message, int _line) {
		super(_message);
		message = _message;
		line = _line;
	}
	
	public VarNameError(String _message) {
		super(_message);
		message = _message;
	}
}