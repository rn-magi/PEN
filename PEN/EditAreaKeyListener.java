import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class EditAreaKeyListener implements KeyListener {
	private int SS = 0;
	private int SN = 0;
	
	private boolean shift_flag = true;
	
	private JTextArea edit_area;
	private EditSelection edit_selection;
	
	public EditAreaKeyListener(JTextArea ea){
		edit_area = ea;
		edit_selection = new EditSelection(edit_area);
	}
		
	public void keyPressed(KeyEvent e){
		SS = edit_area.getSelectionStart();
		SN = edit_area.getSelectionEnd();
		
		if( e.getKeyCode() == KeyEvent.VK_SHIFT )
			shift_flag = false;
		
		if( shift_flag ) {
			switch(e.getKeyCode()){
				case KeyEvent.VK_LEFT :
					if(SS != SN)
						edit_area.setCaretPosition(SS);
					break;
				case KeyEvent.VK_RIGHT :
					if(SS != SN)
						edit_area.setCaretPosition(SN);
					break;
				case KeyEvent.VK_BACK_SPACE	:
					if(SS == SN)
						edit_selection.selection(-1);
					break;
				case KeyEvent.VK_DELETE		:
					if(SS == SN)
						edit_selection.selection(1);
					break;
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if( e.getKeyCode() == KeyEvent.VK_SHIFT )
			shift_flag = true;
		
		if( shift_flag ) {
			switch(e.getKeyCode()){
				case KeyEvent.VK_LEFT	:
				case KeyEvent.VK_RIGHT	:
				case KeyEvent.VK_DOWN	:
				case KeyEvent.VK_UP		:
					edit_selection.selection();
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		String get = "", add = "", add2 = "";
		get += e.getKeyChar();
		if(get.equals("\n")){
				try {
					int rows	= edit_area.getLineCount();
					int pos		= edit_area.getCaretPosition();
					int line	= edit_area.getLineOfOffset(pos);
					int last	= edit_area.getLineEndOffset(line);

					if(pos == last - 1){
						add = new EditAreaAddTab().AddTab(edit_area.getLineStartOffset(line-1),edit_area.getLineEndOffset(line-1) - 1, edit_area);
						if(rows > line + 1){
							add2 = new EditAreaAddTab().AddTab(edit_area.getLineStartOffset(line+1),edit_area.getLineEndOffset(line+1) - 1, edit_area);
						}
						if(add.length() > add2.length()){
							edit_area.insert(add, pos);
						}else{
							edit_area.insert(add2, pos);
						}
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
		}else if(get.equals("\t")){
			int pos = edit_area.getCaretPosition();
			edit_area.insert("  | ",pos);
			edit_area.replaceRange("",pos-1,pos);
		}
	}
}
