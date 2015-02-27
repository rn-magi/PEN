package pen.GUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringReader;

import javax.swing.JTextArea;

import pen.Interpreter.ja.IntVConvertArduino;
import pen.Interpreter.ja.IntVParser;
import pen.Interpreter.ja.ParseIsolateCause;

public class xDCNL2ArduinoConvertListener implements ActionListener {
	private MainGUI gui;
	private PenProperties penPro;

	public xDCNL2ArduinoConvertListener(MainGUI gui) {
		this.gui = gui;
		this.penPro = gui.penPro;
	}

	public void actionPerformed(ActionEvent ae) {
		MyJMenuItem mi = ((MyJMenuItem) ae.getSource());
		
		boolean convertMode = mi.getText().equals("プログラムの変換");
		boolean upMode = mi.getText().equals("Arduinoへの書き込み");
		
		gui.consoleAppend.clean(ConsoleAppend.CONSOLE);
		
		if(penPro.getArduinoCheck() || convertMode){
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
					parser.getJJTree().rootNode().jjtAccept(visitor, null);
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
