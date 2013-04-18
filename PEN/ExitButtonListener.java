import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class ExitButtonListener  implements ActionListener{
	private JFileChooser file_c;
	private PenFrame window;
	private JTextArea edit_area;
	private MainGUI gui;
	
	private String[] obj = {"終了する前にプログラムを保存しますか？"};
	private String[] option = { "保存", "破棄", "取り消し" };
	
	public ExitButtonListener(MainGUI gui) {
		this.gui	= gui;
		file_c		= gui.fc;
		window		= gui.main_window;
		edit_area	= gui.edit_area;
	}

	public void actionPerformed(ActionEvent e) {
		String window_name;
		window_name = window.getTitle();
		if(window_name.substring(0, 1).equals("*")){
			java.awt.Toolkit.getDefaultToolkit().beep();
			int retValue = JOptionPane.showOptionDialog((JFrame)window, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
			if(retValue==JOptionPane.YES_OPTION){
				int returnVal;
				returnVal = gui.fc.showSaveDialog((JFrame)window);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					new FileSave(file_c.getSelectedFile(), edit_area);
					System.exit(0);
				}
			}else if(retValue==JOptionPane.NO_OPTION){
				System.exit(0);
			}
		}else{
			System.exit(0);
		}
	}
}
