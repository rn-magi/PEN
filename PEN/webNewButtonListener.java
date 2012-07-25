import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class webNewButtonListener implements ActionListener{
	private MainGUI gui;
	private String[] obj		= {"編集画面を初期化しますか？"};
	private String[] option	= { "初期化", "取り消し"};
	
	public webNewButtonListener(MainGUI gui){
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) {
		int retValue = JOptionPane.showOptionDialog((WebMain)gui.main_window, obj,"", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
		if(retValue==JOptionPane.YES_OPTION){
			new RunClean(gui);
			gui.edit_area.setText("");
		}
	}
}
