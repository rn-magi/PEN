package pen.GUI;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.JMenuItem;

public class MyJMenuItem extends JMenuItem{
	private Font font = new Font("", 0, 12);
	
	public MyJMenuItem(String str){
		setText(str);
		setFont(font);
	}
	
	public MyJMenuItem(String str, Action ac){
		setFont(font);
		setAction(ac);
		setText(str);
	}
}
