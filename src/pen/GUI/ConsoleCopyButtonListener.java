package pen.GUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

/**
 * メニュー -> 編集 -> 実行画面をコピー
 * を実装しているクラス
 * 
 * @author Ryota Nakamura
 */
public class ConsoleCopyButtonListener implements ActionListener {
	private JTextArea console;
	
	public ConsoleCopyButtonListener(JTextArea cn){
		console		= cn;
	}
	
	public void actionPerformed(ActionEvent e) {
		new ClipBoard(console.getText());
	}

}
