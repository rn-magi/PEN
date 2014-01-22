import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringReader;

import javax.swing.JTextArea;

public class xDCNL2ArduinoConvertListener implements ActionListener {
	private JTextArea edit_area;
	private PenProperties penPro;

	public xDCNL2ArduinoConvertListener(JTextArea edit_area, PenProperties penPro) {
		this.edit_area = edit_area;
		this.penPro = penPro;
	}

	public void actionPerformed(ActionEvent e) {
		if(penPro.containsKey(penPro.Arduino_EXEC_PATH)){
			File pathCheck = new File(penPro.getProperty(penPro.Arduino_EXEC_PATH));
			if(pathCheck.isFile() || 
					(System.getProperty("os.name").indexOf("Mac")>=0
					&& pathCheck.isDirectory())){
				try {
					String str = edit_area.getText() + "\n";
		
					IntVParser parser = new IntVParser(new StringReader(str));
					parser.disable_tracing();
					parser.IntVUnit();
					IntVConvertArduino visitor = new IntVConvertArduino(penPro);
					parser.jjtree.rootNode().jjtAccept(visitor, null);
				} catch (ParseException e1) {
				}
			} else {
				// TODO ArduinoIDEのパスが間違えているときの処理
			}
		} else {
			// TODO ArduinoIDEのパスが設定されていないときの処理
		}
	}
}
