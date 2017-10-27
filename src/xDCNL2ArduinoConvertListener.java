import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class xDCNL2ArduinoConvertListener implements ActionListener {
	private MainGUI gui;
	private PenProperties penPro;
	private JTextArea text;
	private xDNCL2Arduino convert;

	public xDCNL2ArduinoConvertListener(MainGUI gui) {
		this.gui = gui;
		this.penPro = gui.penPro;
	}
	
	public xDCNL2ArduinoConvertListener(MainGUI gui, xDNCL2Arduino convert) {
		this.gui = gui;
		this.penPro = gui.penPro;
		this.convert = convert;
	}
	
	public xDCNL2ArduinoConvertListener(MainGUI gui, JTextArea text) {
		this.gui = gui;
		this.penPro = gui.penPro;
		this.text = text;
	}

	public void actionPerformed(ActionEvent ae) {
		boolean convertMode = false;
		boolean upMode = false;
		
		if( ae.getSource() instanceof JButton ) {
			System.out.println("aaa");
			convertMode = true;
			upMode = true;
			gui.edit_area.setText(text.getText());
		} else {
			MyJMenuItem mi = ((MyJMenuItem) ae.getSource());
		
			if(mi.getText().equals("プログラムの変換")) {
				convertMode = true;
				upMode = false;
			} else if(mi.getText().equals("Arduinoへの書き込み")) {
				convertMode = true;
				upMode = true;
			} else if(mi.getText().equals("モード変更")) {
				convert.setVisible(true);
				gui.main_window.setVisible(false);
			}
		}
			
		gui.consoleAppend.clean(ConsoleAppend.CONSOLE);
		
		if(penPro.getArduinoCheck() && convertMode){
			String str = gui.edit_area.getText() + "\n";
			
			try {
				gui.edit_area.setSelectionStart(0);
				gui.edit_area.setSelectionEnd(0);
				
				IntVParser parser = new IntVParser(new StringReader(str));
				parser.setMainGUI(gui);
				if(gui.Flags.DebugFlag) {
					parser.enable_tracing();
				} else {
					parser.disable_tracing();
				}
				parser.IntVUnit();
				
				try {
					IntVConvertArduino visitor = new IntVConvertArduino(penPro);
					visitor.setUploadFlag(upMode);
					parser.jjtree.rootNode().jjtAccept(visitor, null);
					gui.consoleAppend.appendAll("xDNCLからArduinoプログラムへの変換は正常に完了しました\n");
				} catch (Exception e) {
					
				}
			} catch (Exception e) {
				gui.consoleAppend.appendAll(ParseIsolateCause.isolateCause(e));
			} catch (Error e){
				gui.consoleAppend.appendAll(ParseIsolateCause.isolateCause(e));
			} finally {
				gui.consoleAppend.appendAll("\n--------\n");
			}
		} else {
			// TODO ArduinoIDEのパスが間違えているときの処理
		}
	}
}
