import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

/**
 * プログラム実行時の入力処理を行うクラス
 * 
 * @author Ryota Nakamura
 */
public class ConsoleKeyListener implements KeyListener{
	private String input_line;
	private int start_offs = 0;
	private JTextArea textArea;
	private MainGUI gui;

	public ConsoleKeyListener(MainGUI gui, JTextArea text){
		this.gui = gui;
		textArea = text;
	}
	
	public int getStartOffset(){
		return start_offs;
	}
	
	public void setStartOffset(int offset){
		start_offs = offset;
	}
	
	public String getInputLine(){
		return input_line;
	}
	
	public void keyPressed(KeyEvent e){
		if(gui.Flags.RunFlag && gui.Flags.InputFlag){
			switch(e.getKeyCode()){
				case KeyEvent.VK_ENTER :
					textArea.setEditable(false);
					gui.Flags.InputFlag = false;
					try {
						int get_line	= textArea.getLineCount();
						//int start_offs	= textArea.getLineStartOffset(get_line - 1);
						int end_offs	= textArea.getLineEndOffset(get_line - 1);
						input_line	= textArea.getText(start_offs , end_offs - start_offs);
						textArea.append("\n");
					} catch (BadLocationException f) {
						f.printStackTrace();
					}
					break;
				default:
					try {
						int console_line = textArea.getLineCount();
						int key_line = textArea.getLineOfOffset(textArea.getSelectionStart()) + 1;
						if(console_line != key_line){
							Document doc		= textArea.getDocument();
							Position EndPos	= doc.getEndPosition();
							int pos		= EndPos.getOffset();
							textArea.getCaret().setDot(pos);
							textArea.requestFocus();
						}
					} catch (BadLocationException f) {
						f.printStackTrace();
					}
					break;
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
	public void keyTyped(KeyEvent e) {
		/*
		String get = "";
		get += e.getKeyChar();
		if(get.equals("\b")){
			int pos = textArea.getCaretPosition();
			if(start_offs >= pos) {
				textArea.insert(" ",pos);
			}
		}
		*/
	}
}

