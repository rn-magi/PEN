public class ParseError extends Error{
	public String message;
	
	public ParseError(String message){
		this.message = message;
	}
}
