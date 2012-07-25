import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class MyWindowAdapter extends WindowAdapter{
	MainGUI gui;
	private PenFrame window;
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
			String window_name;
			if (window instanceof JFrame) {
				window_name = ((JFrame)window).getTitle();
			} else {
				window_name = ((WebMain)window).getTitle();
			}
			if(window_name.substring(0, 1).equals("*")){
				java.awt.Toolkit.getDefaultToolkit().beep();
				int retValue;
				if (window instanceof JFrame) {
					retValue = JOptionPane.showOptionDialog((JFrame)window, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
				} else {
					retValue = JOptionPane.showOptionDialog((WebMain)window, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
				}
				if(retValue==JOptionPane.YES_OPTION){
					int returnVal;
					if (window instanceof JFrame) {
						returnVal = gui.fc.showSaveDialog((JFrame)window);
					} else {
						returnVal = gui.fc.showSaveDialog((WebMain)window);
					}
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						new FileSave(gui.fc.getSelectedFile(), text_write);
						error_dump.exit();
						if (window instanceof JFrame) {
							((JFrame)window).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
						}
					}else{
						if (window instanceof JFrame) {
							((JFrame)window).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						}
					}
				}else if(retValue==JOptionPane.NO_OPTION){
					error_dump.exit();
					if (window instanceof JFrame) {
						((JFrame)window).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					}
				}else if(retValue==JOptionPane.CANCEL_OPTION){
					if (window instanceof JFrame) {
						((JFrame)window).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					}
				}
			} else {
				error_dump.exit();
				if (window instanceof JFrame) {
					((JFrame)window).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				}
			}
		} else {
			java.awt.Toolkit.getDefaultToolkit().beep();
			if (window instanceof JFrame) {
				((JFrame)window).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			}
		}
	}
}