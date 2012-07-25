import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.BadLocationException;

class EditButtonListener implements ActionListener{
	private String[] add;
	MainGUI gui;
	
	public EditButtonListener(String[] apend, MainGUI gui){
		add = apend;
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(!gui.Flags.RunFlag){
			String tab = Tab();
			String str = "";
			for(int i = 0; i < add.length; i++){
				if(i == 0){
					str += add[i];
				}else{
					str += tab + add[i];
				}
			}
			gui.edit_area.replaceRange(str,gui.edit_area.getSelectionStart(),gui.edit_area.getSelectionEnd());
			gui.edit_area.requestFocus();
		}
	}
	
	public String Tab(){
		String add_tab = "";
		try {
			int pos		= gui.edit_area.getCaretPosition();
			int line	= gui.edit_area.getLineOfOffset(pos);
			
			add_tab = new EditAreaAddTab().AddTab(gui.edit_area.getLineStartOffset(line), gui.edit_area.getLineEndOffset(line) - 1, gui.edit_area);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		return add_tab;
	}
}
