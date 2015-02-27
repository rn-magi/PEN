package pen.GUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class FileSaveButtonListener implements ActionListener{
	private JFileChooser file_c;
	private MyJFrame window;
	private JTextArea edit_area;
	private String WindowName;

	public FileSaveButtonListener(MainGUI gui){
		file_c		= gui.fc;
		window		= gui.main_window;
		edit_area	= gui.edit_area;
		WindowName	= gui.WindowName;
	}
	
	public void actionPerformed(ActionEvent e) {
		int returnVal = file_c.showSaveDialog(window);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = file_c.getSelectedFile();
			String file_name = new FileSave(file, edit_area, file_c, window).getFileName();
			if(file_name != null){
				window.setTitle(file_name + " - " + WindowName);
			}
		}
	}
}
