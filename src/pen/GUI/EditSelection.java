package pen.GUI;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class EditSelection {
	private String l	= "≪";
	private String r	= "≫";
	private String tab	= "  | ";
	private JTextArea edit_area;
	private int position = 0;
	private int move = 0;
	
	public EditSelection(JTextArea ea){	
		edit_area = ea;
	}
	
	public void selection(int move){
		this.move = move;
		position = edit_area.getCaretPosition() + move;
		if(position > 0){
			selection();
		} else {
			position = 0;
		}
	}
	
	public void selection(){
		int pos;
		
		if(position == 0) {
			pos = edit_area.getCaretPosition();
		} else {
			pos = position;
			position = 0;
		}
		int l_pos = -1;
		int r_pos = -1;
		int end_pos = 0;
		String temp;
		
		try {
			end_pos = edit_area.getLineEndOffset(edit_area.getLineCount() - 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		if(move != 0) {
			if(pos - tab.length() + 1 >= 0 && move == -1){
				try {
					temp = edit_area.getText(pos - tab.length() + 1, tab.length());
					if(tab.equals(temp)){
						l_pos = pos - tab.length() + 1;
						r_pos = pos;
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else if(pos + tab.length() - 1 <= end_pos && move == 1){
				try {
					temp = edit_area.getText(pos - 1, tab.length());
					if(tab.equals(temp)){
						l_pos = pos - 1;
						r_pos = pos + tab.length() -1;
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		for(int x = 1; x < 7 && pos - x >= 0; x++){
			try {
			    temp = edit_area.getText(pos - x, 1);
				if(l.equals(temp)){
					l_pos = pos - x;
					break;
				}else if(r.equals(temp)){
					break;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		if(l_pos != -1){
			for(int x = 0; x < 6 && pos + x <= end_pos; x++){
				try {
					if(r.equals(edit_area.getText(pos + x, 1))){
						r_pos = pos + x + 1;
						break;
					}
				} catch (BadLocationException f) {
					f.printStackTrace();
				}
			}

			if(r_pos != -1){
				edit_area.setSelectionStart(l_pos);
				edit_area.setSelectionEnd(r_pos);
			}
		}
		move = 0;
	}
}
