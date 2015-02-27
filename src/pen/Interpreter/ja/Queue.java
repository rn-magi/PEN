package pen.Interpreter.ja;
import java.util.Vector;

/**
 * 作成日: 2006/11/20
 * 
 * Queue を実現するためのクラス
 *
 * @author Ryota Nakamura
 **/
public class Queue extends Vector{
	public void push(Object obj) {
		super.addElement(obj);
	}

	public Object pop() {
		try {
			Object obj = super.elementAt(0);
			super.removeElementAt(0);
			return(obj);
		} catch(Exception exception) {
			return(null);
		}
	}
}