import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class HelpPenButtonListener implements ActionListener {
	private String str;
	private JLabel info;
	
	public HelpPenButtonListener(String ver){
		str =	"<html><pre>" +
				"初学者向けのプログラミング学習環境 PEN " + ver + "\n" +
				"" + "\n" +
				"PENは初学者向けのプログラミング学習環境です。" + "\n" +
				"PENの記述言語は、" + "\n" +
				"  大学入試センターのアルゴリズム記述言語 DNCL" + "\n" +
				"に拡張を行った xDNCL言語 を用いています。" + "\n" +
				"詳しくは [ ./Manual/xDNCL-Language-Manual.pdf ] を参照してください。" + "\n" +
				"" + "\n" +
				"" + "\n" +
				"Copyright(c) 2003-2017" + "\n" +
				"中村 亮太" + "\n" +
				"</pre></html>";
		
		info = new MyJLabel(str);
	}
	
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, info, "PEN について", JOptionPane.INFORMATION_MESSAGE);
	}
}
