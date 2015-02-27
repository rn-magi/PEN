package pen.GUI;
import java.awt.Color;

public class MyRunnable implements Runnable {
	private MainGUI gui;
	
	public MyRunnable(MainGUI gui){
		this.gui = gui;
	}
	
	public void run() {
		gui.consoleAppend.appendAll("\n--------\n");

		if(gui.MenuBar != null){ gui.MenuBar.MysetEnabled(true); }
		gui.edit_area.setEditable(true);
		gui.edit_area.setBackground(new Color(255,255,255));
		gui.numbar_area.setBackground(new Color(255,255,255));
		gui.new_button.setEnabled(true);
		gui.save_button.setEnabled(true);
		gui.open_button.setEnabled(true);

		if(gui.Flags.StopFlag){
			new RunClean(gui);
		} else {
			if(gui.Flags.BugFlag){
				if(gui.Flags.RunFlag) {
					gui.error_dump.state();
				}
				gui.run_button.setText("実行");
				gui.run_print.breaks();
			} else {
				gui.run_point.insert("\n", 0);
				gui.run_button.setText("始めから実行");
				gui.run_print.end();
			}
			gui.Flags.StepFlag	= false;
			gui.Flags.RunFlag	= false;
			gui.Flags.SuspendFlag	= false;
			gui.Flags.InputFlag	= false;
			gui.Flags.StopFlag	= false;
		}
		gui.penPlugin.destruction();
	}
}
