import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MyJLabel extends JLabel{	
	public MyJLabel(String str){
		Font font = new Font("", 0, 12);
		setText(str);
		setFont(font);
		setAlignmentX(JComponent.CENTER_ALIGNMENT);
	}
	
	public MyJLabel(String str, Dimension size){
		Font font = new Font("", 0, 12);
		setText(str);
		setFont(font);
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public MyJLabel(String str, Font font, Dimension size){
		setText(str);
		setFont(font);
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
	}
	
	public MyJLabel(Dimension size){
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
	}
	
	public MyJLabel(EditButtonList ebl) {
		Font font = new Font("", 0, 12);
		setText(ebl.ButtonText);
		setFont(font);
		
		Dimension size = new Dimension(ebl.Width, ebl.Height);
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
		
		setBackground(ebl.Color);
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}
}