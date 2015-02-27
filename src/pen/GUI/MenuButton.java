package pen.GUI;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;

public class MenuButton extends JButton{
	private Font font = new Font("Monospaced", 0, 12);
	
	public MenuButton(String text, int w, int h){
		Dimension size = new Dimension(w, h);

		setMargin(new Insets(1,1,1,1));
		setText(text);
		setFont(font);
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
	}
}