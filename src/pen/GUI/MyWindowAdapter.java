package pen.GUI;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class MyWindowAdapter extends WindowAdapter{
	private MainGUI gui;
	private MyJFrame window;
	private JTextArea text_write;
	private ErrorSave error_dump;
	private String[] obj = {"終了する前にプログラムを保存しますか？"};
	private String[] option = { "保存", "破棄", "取り消し" };
	
	public MyWindowAdapter(MainGUI gui){
		this.gui = gui;
		this.window = gui.main_window;
		this.text_write = gui.edit_area;
		this.error_dump = gui.error_dump;
	}

	public void windowClosing(WindowEvent we) {
		if(!gui.Flags.RunFlag){
			String window_name = window.getTitle();

			if(window_name.substring(0, 1).equals("*")){
				java.awt.Toolkit.getDefaultToolkit().beep();
				int retValue = JOptionPane.showOptionDialog(window, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);

				if(retValue==JOptionPane.YES_OPTION){
					int returnVal = gui.fc.showSaveDialog(window);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						new FileSave(gui.fc.getSelectedFile(), text_write);
						error_dump.exit();
						window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
					}else{
						window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					}
				}else if(retValue==JOptionPane.NO_OPTION){
					error_dump.exit();
					window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				}else if(retValue==JOptionPane.CANCEL_OPTION){
					window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
			} else {
				error_dump.exit();
				window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			}
		} else {
			java.awt.Toolkit.getDefaultToolkit().beep();
			window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}
	}
}