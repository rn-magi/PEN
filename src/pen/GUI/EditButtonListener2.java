package pen.GUI;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

public class EditButtonListener2 implements MouseListener {
	private JPopupMenu OpenWindow;
	private MainGUI gui;
	
	public EditButtonListener2(MainGUI gui, JPopupMenu Window){
		this.gui = gui;
		OpenWindow = Window;
	}
	public void mouseClicked(MouseEvent e) {
		if(!gui.Flags.RunFlag){
			OpenWindow.show(e.getComponent(), e.getX(), e.getY()-100);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
