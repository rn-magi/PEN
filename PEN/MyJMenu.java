import java.awt.Font;

import javax.swing.JMenu;

public class MyJMenu extends JMenu{
	public MyJMenu(String str){
		Font font = new Font("Monospaced", 0, 12);
		setText(str);
		setFont(font);
	}
}
