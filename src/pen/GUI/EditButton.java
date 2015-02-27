package pen.GUI;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

public class EditButton extends JButton{
	private Font font = new Font("", 0, 10);
	
	public EditButton(EditButtonList ebl, MainGUI gui){
		setProperties(ebl);
		addActionListener(new EditButtonListener(ebl.AppendText, gui));
	}
	
	public EditButton(EditButtonList ebl, MainGUI gui, JPopupMenu FloatWindow){
		setProperties(ebl);
		addMouseListener(new EditButtonListener2(gui, FloatWindow));
	}
	
	public void setProperties(EditButtonList ebl){
		setMargin(new Insets(0,0,0,0));
		setText(ebl.ButtonText);
		setFont(font);
		setToolTipText(ebl.TipText);
		
		Dimension size = new Dimension(ebl.Width, ebl.Height);
		setMaximumSize(size);
		setMinimumSize(size);
		setPreferredSize(size);
		
		setBackground(ebl.Color);
	}
}