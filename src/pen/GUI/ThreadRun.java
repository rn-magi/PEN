import java.io.StringReader;

import javax.swing.SwingUtilities;

public class ThreadRun extends Thread {
	private MainGUI gui;
	private IntVExecuter visitor;
	
	public ThreadRun(MainGUI gui){
		this.gui = gui;
	}
	
	public void run() {
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
			gui.penPlugin.init();

			try {
				visitor = new IntVExecuter(gui);
				if(gui.Flags.DebugFlag) { visitor.NodeDump(); }
				parser.jjtree.rootNode().jjtAccept(visitor, null);
			} catch(ConditionFormatException e) {
				gui.Flags.BugFlag = true;
				gui.consoleAppend.appendAll("### " + e.line + "行目あたりの条件式を見直してください\n");
			} catch (NumberFormatException e){
				gui.Flags.BugFlag = true;
				gui.consoleAppend.appendAll("### 不正な文字が入力されました\n");
				gui.consoleAppend.appendAll("### もしくは型変換に失敗しました\n");
			} catch (NullPointerException e) {
				gui.Flags.BugFlag = true;
			//	gui.consoleAppend.appendAll("### 変数が宣言されていません\n");
			} catch (ArithmeticException e){
				gui.Flags.BugFlag = true;
				gui.consoleAppend.appendAll("### 演算のエラーです\n");
				gui.consoleAppend.appendAll("### ゼロで除算していませんか？\n");
			} catch (StringIndexOutOfBoundsException e){
				gui.Flags.BugFlag = true;
				gui.consoleAppend.appendAll("### 文字列操作に失敗しました\n");
				gui.consoleAppend.appendAll("### " + e.getLocalizedMessage() + "\n");
			} catch (ArrayIndexOutOfBoundsException e){
				gui.Flags.BugFlag = true;
				gui.consoleAppend.appendAll("### 配列のサイズ以上を指定しました\n");
				gui.consoleAppend.appendAll("### " + e.getLocalizedMessage() + "\n");
			} catch (Exception e) {
				gui.Flags.BugFlag = true;
				gui.consoleAppend.appendAll("### 実行できませんでした (実行時エラー)\n");
				gui.consoleAppend.appendAll("### エラーコード：" + e + "\n");
			}
		} catch (Exception e) {
			gui.Flags.BugFlag = true;
			gui.consoleAppend.appendAll(ParseIsolateCause.isolateCause(e));
		} catch (Error e){
			gui.Flags.BugFlag = true;
			gui.consoleAppend.appendAll(ParseIsolateCause.isolateCause(e));
		}
		
		SwingUtilities.invokeLater(new MyRunnable(gui));
	}
	
	public void ThreadStop(){
		visitor.mysleep_stop();
	}
}