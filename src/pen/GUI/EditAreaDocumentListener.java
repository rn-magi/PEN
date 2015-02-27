package pen.GUI;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class EditAreaDocumentListener implements DocumentListener{
	private int line = 1;
	private int text_line;
	private int num_line;
	private int start_offs;
	private int end_offs;

	private MyJFrame main_window;
	private JTextArea edit_area;
	private JTextArea numbar_area;
	
	public EditAreaDocumentListener(MyJFrame mw, JTextArea ea, JTextArea na){
		main_window = mw;
		edit_area = ea;
		numbar_area = na;
	}
	
	public void changedUpdate(DocumentEvent e) {
	}
	
	public void insertUpdate(DocumentEvent e) {
		text_line = edit_area.getLineCount();
		if(line < text_line){
			num_line	=numbar_area.getLineCount();
			for(int i = num_line; i <= text_line; i++){
				if( i < 10){
					numbar_area.append("  ");
				}else if(i < 100){
					numbar_area.append(" ");
				}
				numbar_area.append(Integer.toString(i) + ":\n");
			}
			line = text_line;
		}
		WindowTitleUpdate();
	}
	
	public void removeUpdate(DocumentEvent e) {
		text_line = edit_area.getLineCount();
		if(line > text_line){
			try {
				num_line	= numbar_area.getLineCount();
				start_offs	= numbar_area.getLineStartOffset(text_line);
				end_offs	= numbar_area.getLineEndOffset(num_line-1);
				numbar_area.replaceRange("",start_offs,end_offs);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			line = text_line;
		}
		WindowTitleUpdate();
	}
	
	public void WindowTitleUpdate(){
		String window_name;
		window_name = main_window.getTitle();
		if(!window_name.substring(0, 1).equals("*")){
			main_window.setTitle("*" + window_name);
		}
	}
}