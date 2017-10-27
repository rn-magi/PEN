import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TooManyListenersException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class xDNCL2Arduino extends WindowAdapter{
	public JFrame window = new JFrame("Arduinoへの書き込み専用ウィンドウ");
	public JTextArea text = new JTextArea();
	public JButton button = new JButton("Arduinoへの書き込み");
	private MainGUI gui;
	private String programText;
	
	public xDNCL2Arduino(MainGUI gui) {
		this.gui = gui;
		button.addActionListener(new xDCNL2ArduinoConvertListener(gui, text));
		button.setFont(new Font("SansSerif", Font.PLAIN, 32));
		window.setSize(800, 600);
		window.addWindowListener(this);
		window.getContentPane().add(text,BorderLayout.CENTER);
		window.getContentPane().add(button,BorderLayout.SOUTH);
		
		try {
			DropTarget text_drop = new DropTarget();
			text_drop.addDropTargetListener(new xDNCL2ArduinoFileDropOpen(text));
			text.setDropTarget(text_drop);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}
	
	public void setVisible(boolean flag) {
		window.setVisible(flag);
		if(flag) {
			programText = gui.edit_area.getText();
		}
	}
	
	public void windowClosing(WindowEvent we) {
		gui.main_window.setVisible(true);
		this.setVisible(false);
		
		gui.edit_area.setText(programText);
		text.setText("");
	}
}
