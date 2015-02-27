package pen.GUI;
public class RunClean {
	public RunClean(MainGUI gui){
		gui.consoleAppend.clean(ConsoleAppend.CONSOLE);
	//	int x = MainGUI.vt_model.getRowCount();
	//	for(int i = 0; i < x; i++){
	//		MainGUI.vt_model.removeRow(0);
	//	}
		gui.vt_model.setRowCount(0);
		gui.run_print.breaks();
		gui.Flags.StopFlag = false;
		gui.run_button.setText("実行");
		gui.run_point.setText("●");
		gui.edit_area.requestFocus();
		gui.gDrawWindow.gCloseWindow();
	}
}
